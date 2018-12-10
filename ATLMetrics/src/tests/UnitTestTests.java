package tests;

import org.junit.Test;

import exceptions.FileOrFolderNotFoundException;
import exceptions.MissingParameterException;
import unitTestFramework.UnitTester;

public class UnitTestTests {

	@Test(expected = FileOrFolderNotFoundException.class)
	public void createUnitTestNotFoundFolder() throws Exception{
		new UnitTester("wrong");
	}
	
	@Test(expected = FileOrFolderNotFoundException.class)
	public void createUnitTestEmptyFolder() throws Exception{
		new UnitTester("trafosTest/Empty");
	}
	
	@Test(expected = FileOrFolderNotFoundException.class)
	public void createUnitTestOnlyATLFolder() throws Exception{
		new UnitTester("trafosTest/onlyATL");
	}
	
	@Test(expected = MissingParameterException.class)
	public void createUnitTestMissingInfo() throws Exception{
		new UnitTester("trafosTest/missingInfo");
	}
	
	@Test
	public void createUnitTesterHSM3FSM() throws Exception{
		UnitTester unittester = new UnitTester("trafosTest/HSM2FSM");
		unittester.generateGrimmConfigParamsFiles();
	}
}
