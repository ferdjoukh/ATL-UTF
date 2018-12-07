package tests;

import org.junit.Test;

import exceptions.FileOrFolderNotFoundException;
import unitTestFramework.UnitTester;

public class UnitTestTests {

	@Test(expected = FileOrFolderNotFoundException.class)
	public void createUnitTestNotFoundFolder() throws Exception{
		UnitTester unittester = new UnitTester("wrong");
		System.out.println("aa");
	}

}
