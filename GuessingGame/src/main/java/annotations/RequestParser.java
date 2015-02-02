package annotations;

import models.Request;
import models.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by christoffer.gunning on 2015-01-29.
 */
public class RequestParser {
    public Response parse(Class<?> clazz, Request request) throws Exception {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Path.class)) {
                Annotation annotation = method.getAnnotation(Path.class);
                Path path = (Path) annotation;
                if(!path.value().equals(request.getPageRequested()))
                    continue;
                if (request.getType() == Request.Type.GET && method.isAnnotationPresent(GET.class)) {
                    System.out.println(method);
                    try {
                        return (Response) method.invoke(clazz, request);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("NEHE");
                    }
                } else if (request.getType() == Request.Type.POST && method.isAnnotationPresent(POST.class)) {
                    System.out.println(method);
                    try {
                        return (Response) method.invoke(clazz, request);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("NEHE");
                    }
                }
            }
        }

        // Vi verkar ha misslyckats!

        for (Method method : methods) {
            if (method.isAnnotationPresent(Default.class)) {
                return (Response) method.invoke(clazz, request);
            }
        }

        // Ska aldrig hända
        return null;
    }
}
