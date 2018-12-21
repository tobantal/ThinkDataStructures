/**
 * 
 */
package com.allendowney.thinkdast;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


/**
 * @author downey
 *
 */
public class MyLinkedListTest extends MyArrayListTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);

		mylist = new MyLinkedList<Integer>();
		mylist.addAll(list);
	}

}
