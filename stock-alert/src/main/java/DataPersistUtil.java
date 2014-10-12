import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataPersistUtil {
public static List<String> getData() throws IOException{
	List <String> result= new ArrayList<String>();
	System.out.println(new File("Test.txt").getAbsolutePath());
	BufferedReader br = new BufferedReader(new FileReader("Test.txt"));  
	String data = br.readLine();//一次读入一行，直到读入null为文件结束  
	while( data!=null){   
	      result.add(data);
	      data = br.readLine(); //接着读下一行  
	}    
	br.close();
	return result;
}
public static void putData(Collection<String> args) throws IOException {
	FileWriter fw = new FileWriter("Test.txt");    
    for(String s:args){
    	fw.write(s,0,s.length());	
    	fw.write("\n");
    }
    fw.flush();          
    fw.close();    
}

public static void main(String [] args) throws IOException{
//	ArrayList args2 = new ArrayList();
//	args2.add("asdasdad");
//	args2.add("asdasdad");
//	args2.add("asdasdad");
//	putData(args2);
//	
	System.out.println(getData());
	System.out.println(new File("").getAbsolutePath());
	System.out.println("ghnghnhgn"); 

}

}
