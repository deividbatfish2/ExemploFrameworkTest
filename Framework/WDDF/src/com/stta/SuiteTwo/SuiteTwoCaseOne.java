//Encontre mais tutoriais sobre WebDriver em -> http://software-testing-tutorials-automation.blogspot.com
package com.stta.SuiteTwo;

import java.io.IOException;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

//A classe SuiteOneCaseOne herda da classe SuiteOneBase.
//Ent�o, a classe SuiteOneCaseOne � filha da classe SuiteOneBase e da classe SuiteBase.
public class SuiteTwoCaseOne extends SuiteTwoBase{
	Read_XLS FilePath = null;	
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;
	
	@BeforeTest
	public void checkCaseToRun() throws IOException{
		//Chama a fun��o init() da classe SuiteBase para inicializar os arquivos .xls
		init();	
		//Para definir o caminho do arquivo SuiteOne.xls na variavel FilePath.
		FilePath = TestCaseListExcelTwo;		
		TestCaseName = this.getClass().getSimpleName();
		//Nome da aba para verificar a flag CaseToRun diante do caso de teste.
		SheetName = "TestCasesList";
		//Nome da coluna na aba TestCasesList.
		ToRunColumnNameTestCase = "CaseToRun";
		//Nome da coluna na aba de dados do caso de teste.
		ToRunColumnNameTestData = "DataToRun";
		//A sintaxe a baixo inseri log no arquivo applog.log.
		Add_Log.info(TestCaseName+" : Execution started.");
		
		//Para verificar se a coluna CaseToRun = Y ou N do caso de teste na aba relacionada.
		//Se CaseToRun = N n�o tiver sido preenchida, A execu��o deste caso de teste ser� pulada. Caso contr�rio ele ser� executado.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){			
			//Para reportar que o resultado foi "skip" para o caso de teste na aba TestCasesList.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			//Gerando um exce��o "throw skip exception" para pular este caso de teste.
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}
		//Para recuperar a flag DataToRun definida em todas as linhas dos dados da aba relacionada.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData);
	}
	
	//Receber os dados String das 3 colunas a cada Itera��o.
	@Test(dataProvider="SuiteTwoCaseOneData")
	public void SuiteTwoCaseOneTest(String DataCol1,String DataCol2,String ExpectedResult){
		
		DataSet++;
		
		//Criando objeto da classe testng SoftAssert.
		s_assert = new SoftAssert();		
		
		//Se encontrar DataToRun = "N" definida para a linha esta execu��o ser� "skipped".
		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){
			//Se DataToRun = "N", defina Testskip=true.
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No Or Blank. So Skipping Its Execution.");
		}
		
		//Se encontrar DataToRun = "Y" para a linha as linhas a baixo ser�o executadas.
		//Converter o dado de String para Integer
		int ValueOne = Integer.parseInt(DataCol1);
		int ValueTwo = Integer.parseInt(DataCol2);		
		int ExpectedResultInt =  Integer.parseInt(ExpectedResult);
				
		//Para Inicializar o Navegador.
		loadWebBrowser();
		
		driver.get(Param.getProperty("siteURL")+"/2014/04/calc.html");		
		getElementByName("txt_Result").clear();
		getElementByXPath("btn_Calc_PrePart",ValueOne,"btn_Calc_PostPart").click();
		getElementByID("btn_multiply").click();
		getElementByXPath("btn_Calc_PrePart",ValueTwo,"btn_Calc_PostPart").click();
		getElementByCSS("btn_Equals").click();
		String Result = getElementByName("txt_Result").getAttribute("value");
		int ActualResultInt =  Integer.parseInt(Result);
		if(!(ActualResultInt==ExpectedResultInt)){
			Testfail=true;	
			s_assert.assertEquals(ActualResultInt, ExpectedResultInt, "ActualResult Value "+ActualResultInt+" And ExpectedResult Value "+ExpectedResultInt+" Not Match");
		}
		
		if(Testfail){
			//E por ultimo, test data assertion failure ser�o reportados no testNG reports e ser�o marcados a linha da planilha, o caso de teste e a Suite de teste como fail.
			s_assert.assertAll();		
		}
	}
	
	//@AfterMethod este m�todo ser� executado ap�s a execu��o do metodo @Test a cada vez.
	@AfterMethod
	public void reporterDataResults(){		
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			//Se encontrar Testskip = true, O resultado ser� reportado como SKIP na linha referente da aba da planilha.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}	
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			//Para tornar o objeto refer�ncia nulo ap�s reportar no arquivo report.
			s_assert = null;
			//Define TestCasePass = false para reportar o caso de teste como fail na aba do excel.
			TestCasePass=false;
			//Se encontrar Testfail = true, o resultado ser� reportado como FAIL na linha referente da aba da planilha.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");			
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			//Se encontrar Testskip = false e Testfail = false, o resultado ser� reportado como PASS na linha referente da aba da planilha.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
		}
		//Ap�s definit ambas as flags como false para o proximo data set.
		Testskip=false;
		Testfail=false;
	}
	
	//Este metodo data provider retornar� os dados das 3 colunas uma a uma a cada itera��o.
	@DataProvider
	public Object[][] SuiteTwoCaseOneData(){
		//Para retornar dados da coluna Data 1, da coluna 2 e a coluna Expected Result da aba SuiteOneCaseOne.
		//As ultimas duas colunas (DataToRun and Pass/Fail/Skip) ser�o ignoradas programaticamente quando lendo os dados do teste.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName);
	}

	//Para reportar se o resultado pass ou fail para o caso de teste na aba TestCasesList.
	@AfterTest
	public void closeBrowser(){
		//Para fechar o navegador ap�s o final do teste.
		closeWebBrowser();
		if(TestCasePass){
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");			
		}		
	}
}