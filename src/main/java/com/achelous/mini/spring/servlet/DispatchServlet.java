package com.achelous.mini.spring.servlet;

import com.achelous.mini.spring.annotation.Autowire;
import com.achelous.mini.spring.annotation.Controller;
import com.achelous.mini.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: fanJiang
 * @Date: Create in 10:52 2018/4/22
 */
public class DispatchServlet extends HttpServlet {


    private Properties contextConfig = new Properties();

    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    private List<String> classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 开始进行初始化

        // 1.定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        // 2.加载
        doScanner(contextConfig.getProperty("scanPackage"));


        // 3.注册
        doRegistry();

        // 4.实现自动依赖注入
        doAutowire();


    }

    private void doAutowire() {
        if (beanMap.isEmpty()) {return ;}


        for (Map.Entry<String, Object> entry: beanMap.entrySet()) {


            // 获取实例的所有属性字段
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field: fields) {

                if (!field.isAnnotationPresent(Autowire.class)) { continue; }

                Autowire autowire = field.getAnnotation(Autowire.class);

                String beanName = autowire.value().trim();

                if ("".equals(beanName)) {
                    beanName = field.getType().getSimpleName();
                }

                field.setAccessible(true);


                try {
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }



        }
    }

    private void doRegistry() {

        if (classNames.isEmpty()) {
            return ;
        }
        try {

            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);

                // 只有使用了制定注解的类才会注册   在spring中使用多个子方法来处理
                if (clazz.isAnnotationPresent(Controller.class)) {

                    String beanName = firstLowerCase(clazz.getSimpleName());

                    // 在spring中这里不会直接注入类实例，仅注册beanDefinition
                    // bean实例在依赖注入时实例化
                    beanMap.put(beanName, clazz.newInstance());

                } else if (clazz.isAnnotationPresent(Service.class)) {

                    Service service = clazz.getAnnotation(Service.class);

                    String beanName = service.value();
                    if ("".equals(beanName)) {
                        beanName = firstLowerCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();

                    beanMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();

                    for (Class<?> inter : interfaces) {

                        // 在实例有接口的情况下   将接口也进行注册  注册的value是实现类
                        // 如果有多实现的情况   需使用别名
                        beanMap.put(inter.getSimpleName(), instance);
                    }

                } else {
                    continue ;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String firstLowerCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));

        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                classNames.add(scanPackage + "." +file.getName().replace(".class", ""));
            }
        }

    }

    private void doLoadConfig(String location) {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));

        try {
            // 将servlet中配置的文件路径 读取到context上下文中
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
