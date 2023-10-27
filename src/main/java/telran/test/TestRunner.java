package telran.test;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import telran.beforeeach.annotation.BeforeEach;
import telran.test.annotation.Test;

public class TestRunner implements Runnable {
	private Object testObj;
	private ArrayList<String> namesBefore = new ArrayList<String>();
	public TestRunner(Object testObj) {
		super();
		this.testObj = testObj;
	} 

	@Override
	public void run() {
			Class<?> clazz = testObj.getClass(); 
			Method[] methods = clazz.getDeclaredMethods();
						
			for(Method method: methods) {
				if(method.isAnnotationPresent(BeforeEach.class)) {
				namesBefore.add(method.getName());
				}
				
			}
			System.out.println(namesBefore);
			
			for(Method method: methods) {
				if(method.isAnnotationPresent(Test.class)) {
					method.setAccessible(true);
					callBeforeEach(clazz, namesBefore);
					try {
						
						method.invoke(testObj);
						
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						System.out.println("error: " + e.getMessage());
					}
				}
			} 
	
	}

	private void callBeforeEach(Class<?> clazz, ArrayList<String> namesBefore) {
		namesBefore.stream().forEach(n -> {
			try {
				Method methodBefore = clazz.getDeclaredMethod(n);
				methodBefore.setAccessible(true);
				methodBefore.invoke(testObj);
			} catch (Exception e) {
				System.out.println("er: " + e.getMessage());
			}
		});
	}

}
