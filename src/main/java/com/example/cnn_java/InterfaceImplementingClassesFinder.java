package com.example.cnn_java;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InterfaceImplementingClassesFinder {
    public static List<Class<?>> findImplementingClasses(Class<?> interfaceClass) throws IOException, ClassNotFoundException {
        String packageName = interfaceClass.getPackage().getName();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            List<Class<?>> classes = findClasses(directory, packageName, interfaceClass);
          assert classes != null;
          if (!classes.isEmpty())
                return classes;
        }
        return null;
    }

    private static List<Class<?>> findClasses(File directory, String packageName, Class<?> interfaceClass) throws ClassNotFoundException {
        if (!directory.exists()) {
            return null;
        }
        List<Class<?>> names=new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClasses(file, packageName + "." + file.getName(), interfaceClass);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface()) {
                        names.add(clazz);
                    }
                }
            }
        }
        return names;
    }
}
