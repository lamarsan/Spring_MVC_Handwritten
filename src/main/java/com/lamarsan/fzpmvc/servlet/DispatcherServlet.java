package com.lamarsan.fzpmvc.servlet;

import com.lamarsan.fzpmvc.annaotation.*;
import com.lamarsan.fzpmvc.controller.FzpController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * className: DispatcherServlet
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 19:56
 */
public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();

    Map<String, Object> beans = new HashMap<String, Object>();

    Map<String, Object> handlerMap = new HashMap<String, Object>();

    private static final long SERIAL_VERSION_UID = 1L;


    @Override
    public void init(ServletConfig config) {
        //把所有bean扫描  --> 扫描所有class文件
        scanPackage("com.lamarsan");

        //根据扫描的list全类名，进行实例化
        doInstance();

        //根据bean进行依赖注入
        doIoc();

        //fzp/query  ---> method建立映射
        buildUrlMapping();
    }

    /**
     * 扫描
     */
    private void scanPackage(String basePackage) {
        //加载class文件
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        //fzpmvc...
        String[] filesStr = file.list();
        for (String path : filesStr) {
            File filePath = new File(fileStr + path);
            if (filePath.isDirectory()) {
                //如果是文件继续递归扫描
                scanPackage(basePackage + "." + path);
            } else {
                //如果不是文件夹 加入List   com.lamarsan.fzpmvc.xxx.class
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }

    /**
     * 实例化
     */
    private void doInstance() {
        if (classNames.size() <= 0) {
            System.out.println("包扫描失败......");
            return;
        }

        //list的所有class类，对这些类进行遍历
        for (String className : classNames) {
            String cn = className.replace(".class", "");
            try {
                //......Controller
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(LamarController.class)) {
                    //创建控制类
                    Object instance = clazz.newInstance();
                    LamarRequestMapping requestMapping = clazz.getAnnotation(LamarRequestMapping.class);
                    //fzp   拿到路径
                    String rmvalue = requestMapping.value();
                    beans.put(rmvalue, instance);
                } else if (clazz.isAnnotationPresent(LamarService.class)) {
                    LamarService service = clazz.getAnnotation(LamarService.class);
                    Object instance = clazz.newInstance();
                    beans.put(service.value(), instance);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把service注入到controller
     */
    private void doIoc() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有一个实例化类");
        }
        //把map里所有实例化遍历出来
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(LamarController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(LamarAutowired.class)) {
                        LamarAutowired auto = field.getAnnotation(LamarAutowired.class);
                        //FzpServiceImpl
                        String key = auto.value();
                        field.setAccessible(true);
                        try {
                            field.set(instance, beans.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 路径映射
     */
    private void buildUrlMapping() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有类的实例化...");
            return;
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(LamarController.class)) {
                LamarRequestMapping requestMapping = clazz.getAnnotation(LamarRequestMapping.class);
                //fzp
                String classPath = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(LamarRequestMapping.class)) {
                        LamarRequestMapping methodMapping = method.getAnnotation(LamarRequestMapping.class);
                        String methodPath = methodMapping.value();
                        //fzp/query  --->   method
                        handlerMap.put(classPath + methodPath, method);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求路径/fzpmvc/fzp/query
        String uri = req.getRequestURI();
        //fzpmvc
        String context = req.getContextPath();
        //fzp/query
        String path = uri.replace(context, "");
        Method method = (Method) handlerMap.get(path);
        //根据key= /fzp到map去拿
        FzpController instance = (FzpController) beans.get("/" + path.split("/")[1]);
        Object[] args = hand(req, resp, method);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 非策略模式
     */
    private static Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method) {
        //拿到当前执行的方法有哪些参数
        Class<?>[] paramClazzs = method.getParameterTypes();
        //根据参数的个数，new一个参数的数组，将方法里的所有参数赋值到args来
        Object[] args = new Object[paramClazzs.length];
        int argsi = 0;
        int index = 0;
        for (Class<?> paramClazz : paramClazzs) {
            if (ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[argsi++] = request;
            }
            if (ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[argsi++] = response;
            }
            //从0-3判断有没有RequestParam注解，很明显paramClazz为0和1时，不是
            //当为2和3时为@RequestParam，需要解析
            //[@com.lamarsan.fzp.annotation.LamarRequestParam(value=name)]
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if (paramAns.length > 0) {
                for (Annotation paramAn : paramAns) {
                    if (LamarRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                        LamarRequestParam rp = (LamarRequestParam) paramAn;
                        //找到注解里的name和age
                        args[argsi++] = request.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }
}
