package com.acxiom;

import java.util.ArrayList;
import java.util.List;

public class CompanyTest {

	public static void main(String[] args) {
		List<String> a = new ArrayList<String>();
		a.add("a1");
		a.add("a2");
		a.add("a3");
		a.add("a4");

		List<String> b = new ArrayList<String>();
		b.add("a1");
		b.add("a2");

		List<String> c = new ArrayList<String>();
		for (String x : a) {
			c.add(x);
		}
		for (String x : b) {
			c.remove(x);
		}
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
	}
}
