package com.stta.SuiteOne;

import java.io.IOException;
import org.testng.SkipException;
import org.testng.annotations.BeforeSuite;
import com.stta.TestSuiteBase.SuiteBase;
import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

//A classe SuiteOneBase herda da classe SuiteBase.
public class SuiteOneBase extends SuiteBase{
	
	Read_XLS FilePath = null;
	String SheetName = null;
	String SuiteName = null;
	String ToRunColumnName = null;	
	
	//Esta fun��o ser� executada antes dos casos de teste da SuiteOne para verificar a flag SuiteToRun.
	@BeforeSuite
	public void checkSuiteToRun() throws IOException{		
		//Chamando a fun��o init() da classe SuiteBase para inicializar os arquivos .xls.
		init();			
		//Para definir o caminho dos arquivos TestSuiteList.xls na vari�vel FilePath.
		FilePath = TestSuiteListExcel;
		SheetName = "SuitesList";
		SuiteName = "SuiteOne";
		ToRunColumnName = "SuiteToRun";
		
		//A sintaxe a baixo inseri log no arquivo applog.log.
		Add_Log.info("Execution started for SuiteOneBase.");
		
		//Se SuiteToRun !== "y" a SuiteOne ser� pulada (ignorada) na execu��o.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnName,SuiteName)){			
			Add_Log.info("SuiteToRun = N for "+SuiteName+" So Skipping Execution.");
			//Para reportar que a SuiteOne foi 'Pulada' na aba SuitesList  da planilha TestSuiteList.xls se SuiteToRun = N.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Skipped/Executed", SuiteName, "Skipped");
			//A exce��o throw SkipException "pular�" a execu��o dos testes da suite e ela ser� marcada como skipped no testng report.
			throw new SkipException(SuiteName+"'s SuiteToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+SuiteName);
		}
		//Para reportar que a SuiteOne foi 'Executed' na aba SuitesList da planilha TestSuiteList.xls se SuiteToRun = Y.
		SuiteUtility.WriteResultUtility(FilePath, SheetName, "Skipped/Executed", SuiteName, "Executed");		
	}		
}
