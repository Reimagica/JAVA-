package src.main.java.BuildIndex;

import java.io.File;

import java.io.IOException;
import java.sql.*;


import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class SQLtoLucene {

	public static void main(String[] args) {
		Connection connection ;
		Statement stmt;
		ResultSet rs;
		try{
			Class.forName("com.mysql.cj.jdbc.Driver"); 
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadoc","root","root");
			stmt=connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,	
    					ResultSet.CONCUR_UPDATABLE);//15��		
		
			String sql="SELECT * FROM eccn_en_fin";//10��
			rs=stmt.executeQuery(sql);
			
			
			//ָ������Ŀ¼
			Directory d=FSDirectory.open(new File(".\\index").toPath());
			//ָ��������
			Analyzer analyzer=new IKAnalyzer(true);
			
			//�����������ö���//15�� 
			IndexWriterConfig conf=new IndexWriterConfig(analyzer);
			//���Ƿ�ʽ��������
			conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			//׷�ӷ�ʽ��������
			//conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND );
			//BasicIndexDemo bid=new BasicIndexDemo();
		    //��������	   
			
			IndexWriter indexWriter=new  IndexWriter(d, conf);//25��
	
			while(rs.next()){ //����			
			//�˴�������		
			
				System.out.println(rs.getString("Item_Number")+"\n"+ rs.getString("ECCN")+"\n"+
					rs.getString("Description")+"\n"+rs.getString("Item_Prefix")+"\n"+rs.getString("Item")+"\n"+rs.getString("Parent_ID"));
		
				Document doc =new  Document();
				//�����ֶ�
				//ItemNumber��Ȼ˳����
	    		Field ItemNumber=new  Field( "Item_Number",rs.getString("Item_Number"), 
		   					Field.Store.YES, Field.Index.NOT_ANALYZED);//40�� 
	    		doc.add(ItemNumber);
	    		//ECCN�ĵ�����
	    		Field ECCN=new  Field("ECCN",
	    				      rs.getString("ECCN"),
			   				  Field.Store.YES,
			   				  Field.Index.NOT_ANALYZED,
			   				  Field.TermVector.WITH_POSITIONS_OFFSETS); //50��     
	    		doc.add(ECCN);//��ECCN�ֶμ����ĵ�      
	    		//Description�ĵ�����
	    		String description;
	    		if(rs.getString("Description")!=null) {
	    			description = rs.getString("Description");
	    		}else {
	    			description = "none";
	    		}
	    		Field Description=new  Field("Description",
	    					description, //55�� 
			                Field.Store.YES,
			                Field.Index.ANALYZED,
			                Field.TermVector.WITH_POSITIONS_OFFSETS);      
	    		doc.add(Description); 
	    		//Item_Prefix��Ŀ���
	    		String itemID;
	    		if(rs.getString("Item_Prefix")!=null) {
	    			itemID = rs.getString("Item_Prefix");
	    		}else {
	    			itemID = "none";
	    		}
	    		Field Item_Prefix=new  Field("Item_Prefix",
	    				    itemID,  
			                Field.Store.YES,
			                Field.Index.NOT_ANALYZED,
			                Field.TermVector.WITH_POSITIONS_OFFSETS);//80��      
                doc.add(Item_Prefix);
                //Item��Ŀ����
                String content;
	    		if(rs.getString("Item")!=null) {
	    			content = rs.getString("Item");
	    		}else {
	    			content = "none";
	    		}
	    		Field Item=new  Field("Item",
	    				    content,  
			                Field.Store.YES,
			                Field.Index.ANALYZED,
			                Field.TermVector.WITH_POSITIONS_OFFSETS);//80��      
                doc.add(Item);         
	    		//Parent_ID�����
                String parent;
	    		if(rs.getString("Parent_ID")!=null) {
	    			parent = rs.getString("Parent_ID");
	    		}else {
	    			parent = "none";
	    		}
	    		Field Parent_ID=new  Field("Parent_ID",//60�� 
	    				 	parent,  
		                    Field.Store.YES,
		                    Field.Index.NOT_ANALYZED,
		                    Field.TermVector.WITH_POSITIONS_OFFSETS);      
	    		doc.add(Parent_ID);//65��            
	    		
	    		indexWriter.addDocument(doc);//���ĵ���ӵ�������д��     
				
			}
			indexWriter.close();////�ر�������д��
  		    System.out.println("�����������"); //110��
  			
  		    analyzer.close();//�رշ����� //20��
  		
			rs.close();
	
			stmt.close();
			connection.close() ;
			System.out.println("�������ĵ�����="+readDoc(d));	
		    d.close();//�ر�Ŀ¼	 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	static int readDoc(Directory d) throws CorruptIndexException, IOException {
		IndexReader  indexReader=IndexReader.open(d);
		int docNum=indexReader.numDocs();//��ȡ�ĵ�ʵ������		
		indexReader.close();//�ر�������ȡ�� //40��	    
        return docNum; //�����ĵ�ʵ������
	}

  
}
