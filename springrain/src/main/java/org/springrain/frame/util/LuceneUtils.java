package org.springrain.frame.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * lucene 工具类
 * 
 * @author caomei
 *
 */
public class LuceneUtils {

    private static final Logger logger = LoggerFactory.getLogger(LuceneUtils.class);
    
	private LuceneUtils(){
		throw new IllegalAccessError("工具类不能实例化");
	}
	
	// 分词器
	private static Analyzer analyzer = new SmartChineseAnalyzer();

	
	/**
	 * 根据关键词查询某个实体类的索引
	 * 
	 * @param clazz
	 * @param page
	 * @param searchkeyword
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List searchDocument(String rootdir,Class clazz, Page page,
			String searchkeyword) throws Exception {
		LuceneFinder lsc=new LuceneFinder(searchkeyword);
		return searchDocument( rootdir,clazz, page, lsc);
	}
	
	 /**
	  * 根据LuceneSearchClause查询关键字,可以添加where条件和数值类型排序,最常用的接口
	  * @param rootdir
	  * @param clazz
	  * @param page
	  * @param luceneFinder
	  * @return
	  * @throws Exception
	  */
    public static <T> List<T> searchDocument(String rootdir,Class<T> clazz, Page page,
            LuceneFinder luceneFinder) throws Exception {

        if (clazz==null||luceneFinder == null ) {
            return null;
        }
        
        String[] fields= luceneFinder.getFields();
        if(luceneFinder.getFields()==null){
            List<FieldInfo> luceneTokenizedFields = ClassUtils.getLuceneTokenizedFields(clazz);
            List<String> fieldList= luceneFinder.getFieldList();
            for(FieldInfo finfo:luceneTokenizedFields){
                fieldList.add(finfo.getFieldName());
            }
            luceneFinder.setFieldList(fieldList);
            fields= luceneFinder.getFields();
        }

       
        
        Builder builder = new BooleanQuery.Builder();
        
        if(StringUtils.isNotBlank(luceneFinder.getKeyword())){
            // 查询指定字段的转换器
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            // 需要查询的关键字
            BooleanQuery booleanQuery = (BooleanQuery) parser.parse(luceneFinder.getKeyword());
            luceneFinder.getListClause().addAll(booleanQuery.clauses());
        }
        
        List<BooleanClause> listClause = luceneFinder.getListClause();
        
        if(CollectionUtils.isNotEmpty(listClause)){
            for (BooleanClause bc:listClause) {
                builder.add(bc);
            }
        }
        
        return searchDocument(rootdir, clazz, page, builder.build(),luceneFinder.getSort());
    }
    
	
	/**
     * 根据一个属性值查询数据列表
     * @param clazz
     * @param page
     * @param fields
     * @param searchkeyword
     * @return
     * @throws Exception
     */
    public static <T> List<T> searchDocumentByTerm(String rootdir,Class<T> clazz, Page page,
            String key,Object value) throws Exception {

        if (clazz==null||StringUtils.isBlank(key)||value==null) {
            return null;
        }
        LuceneFinder lsc=new LuceneFinder(null);
        lsc.addSearchClause(key, ClassUtils.getReturnType(key, clazz), value);
        return searchDocument(rootdir, clazz, page, lsc);
    }
    
    
    /**
     * 根据entityId 查询一个对象
     * @param rootdir
     * @param clazz
     * @param value
     * @return
     * @throws Exception
     */
    public static <T> T searchDocumentById(String rootdir,Class<T> clazz,String value) throws Exception {

        if (clazz==null||StringUtils.isBlank(value)) {
            return null;
        }
        
        EntityInfo info=ClassUtils.getEntityInfoByClass(clazz);
        if(info==null){
            return null;
        }
        
        
        Page page=new Page(1);
        page.setPageSize(1);
        
        List<T> list=  searchDocumentByTerm(rootdir, clazz, page,info.getPkName(), value);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
          
          return list.get(0);
        
    }


	/**
	 * 根据实体类保存到索引,结合 LuceneSearch和LuceneField注解
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public  static String saveDocument(String rootdir,Object entity)
			throws Exception {
		
		// 索引写入配置
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 获取索引目录文件
		Directory directory = getDirectory( rootdir,entity.getClass());
		if (directory == null) {
			return null;
		}
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		try{
		    Document doc=bean2Document(entity);
	        indexWriter.addDocument(doc);
	        indexWriter.commit();
		}catch(Exception e){
		    logger.error(e.getMessage(),e);
		}finally{
		    indexWriter.close();
		    directory.close();
		}
	    
		
		return null;
	}

	/**
	 * 根据实体类批量保存到索引,结合 LuceneSearch和LuceneField注解
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public static <T> String saveListDocument(String rootdir,List<T> list) throws Exception {
		if (CollectionUtils.isEmpty(list)) {
			return "error";
		}
		
		 // 索引写入配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        Class clazz = list.get(0).getClass();
        // 获取索引目录文件
        Directory directory = getDirectory( rootdir,clazz);
        if (directory == null) {
            return null;
        }
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        try{
            List<Document> listDocument=new ArrayList<>();
	         for (T t : list) {
                Document doc=bean2Document(t);
                listDocument.add(doc);
		     }
	         indexWriter.addDocuments(listDocument);
	         indexWriter.commit();
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }finally{
            indexWriter.close();
            directory.close();
        }
    

		return null;
	}

	/**
	 * 根据Id删除索引
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public  static String deleteDocument(String rootdir,String id, Class clazz)
			throws Exception {
		List<FieldInfo> luceneFields = ClassUtils.getLuceneFields(clazz);
		if (CollectionUtils.isEmpty(luceneFields)) {
			return "error";
		}

		String pkName = ClassUtils.getEntityInfoByClass(clazz).getPkName();
		// 索引写入配置
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 获取索引目录文件
		Directory directory = getDirectory( rootdir,clazz);
		if (directory == null) {
			return null;
		}
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
		// 需要查询的关键字
        Term term = new Term(pkName,id.toString());
        TermQuery luceneQuery = new TermQuery(term);
		try{
    		indexWriter.deleteDocuments(luceneQuery);
    		indexWriter.commit();
		}catch(Exception e){
		    logger.error(e.getMessage(),e);
		}finally{
		    indexWriter.close(); // 记得关闭,否则删除不会被同步到索引文件中
		    directory.close(); // 关闭目录
		}
		
		return null;
	}
	
	/**
	 * 批量删除对象的索引
	 * @param rootdir
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static <T> String deleteListDocument(String rootdir,List<T> list) throws Exception{
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Class clazz = list.get(0).getClass();
        List<String> ids=new ArrayList<>();
        
        for(T t:list){
            Object pkValue_o = ClassUtils.getPKValue(t);
            if(pkValue_o==null){
                continue;
            }
            String pkValue=pkValue_o.toString();
            ids.add(pkValue);
        }
         return deleteListDocument(rootdir, ids, clazz);
	}
	
	

	/**
	 * 根据Id列表,批量删除索引
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String deleteListDocument(String rootdir,List<String> ids, Class clazz)
			throws Exception {
		List<FieldInfo> luceneFields = ClassUtils.getLuceneFields(clazz);
		if (CollectionUtils.isEmpty(luceneFields)) {
			return "error";
		}
		if (CollectionUtils.isEmpty(ids)) {
			return "error";
		}
		String pkName = ClassUtils.getEntityInfoByClass(clazz).getPkName();
		// 索引写入配置
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 获取索引目录文件
		Directory directory = getDirectory( rootdir,clazz);
		if (directory == null) {
			return null;
		}
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
		TermQuery[] listTermQuery=new TermQuery[ids.size()];
		
		for (int i=0;i<ids.size();i++) {
			// 需要查询的关键字
			Term term = new Term(pkName,ids.get(i));
			TermQuery luceneQuery = new TermQuery(term);
			listTermQuery[i]=luceneQuery;
		}
		try{
    		indexWriter.deleteDocuments(listTermQuery);
    		indexWriter.commit();
		}catch(Exception e){
            logger.error(e.getMessage(),e);
        }finally{
            indexWriter.close(); // 记得关闭,否则删除不会被同步到索引文件中
            directory.close(); // 关闭目录
        }

		return null;
	}
	
	/**
	 * 删除一个实体类的所有索引
	 * @param ids
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String deleteAllDocument(String rootdir,Class clazz)
			throws Exception {
		// 索引写入配置
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 获取索引目录文件
		Directory directory = getDirectory( rootdir,clazz);
		if (directory == null) {
			return null;
		}
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
		try{
    		indexWriter.deleteAll();
    		indexWriter.commit();
		}catch(Exception e){
            logger.error(e.getMessage(),e);
        }finally{
            indexWriter.close(); // 记得关闭,否则删除不会被同步到索引文件中
            directory.close(); // 关闭目录
        }
		return null;
	}


	/**
	 * 修改一个对象的索引
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public static String updateDocument(String rootdir,Object entity) throws Exception {
		
	    String pkName = ClassUtils.getEntityInfoByClass(entity.getClass()).getPkName();
        Object pkValue_o = ClassUtils.getPKValue(entity);
        
        if(pkValue_o==null){
            return null;
        }
        
        String pkValue=pkValue_o.toString();
		
		// 索引写入配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        // 获取索引目录文件
        Directory directory = getDirectory( rootdir,entity.getClass());
        if (directory == null) {
            return null;
        }
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Term term = new Term(pkName,pkValue);
     
        try{
            Document doc=bean2Document(entity);
            indexWriter.updateDocument(term, doc);
            indexWriter.commit();
        
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }finally{
            indexWriter.close(); // 记得关闭,否则删除不会被同步到索引文件中
            directory.close(); // 关闭目录
        }
        
		return null;
	}

	/**
	 * 批量修改索引
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static <T> String updateListDocument(String rootdir,List<T> list) throws Exception {

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
    	deleteListDocument(rootdir, list);
		saveListDocument(rootdir, list);
		return null;
	}

	
	   
    /**
     * 根据 Query 和 Sort查询数据列表,暂不对外开放.
     * @param rootdir
     * @param clazz
     * @param page
     * @param query
     * @param sort
     * @return
     * @throws Exception
     */
    private static <T> List<T> searchDocument(String rootdir,Class<T> clazz, Page page,
           Query query,Sort sort) throws Exception {
        // 获取索引目录文件
        Directory directory = getDirectory( rootdir,clazz);
        if (directory == null) {
            return null;
        }

        // 获取读取的索引
        IndexReader indexReader = DirectoryReader.open(directory);
        // 获取索引的查询器
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        List<T> list=null;
        try{
            TopDocs topDocs = null;
            int totalCount = indexSearcher.count(query);
            if (totalCount == 0) {
                return null;
            }
            
            ScoreDoc[] hits=null;
            
            if (page == null) {
                if(sort==null){
                    topDocs = indexSearcher.search(query, totalCount);
                }else{
                    topDocs = indexSearcher.search(query, totalCount,sort);
                }
                
                hits=topDocs.scoreDocs;
               
            } else {
                // 总条数
                page.setTotalCount(totalCount);
                int start = page.getPageSize() * (page.getPageIndex() - 1);
                if (start >= totalCount) {
                    return null;
                }
                
                int end= page.getPageSize()*page.getPageIndex();
                
                if(end>totalCount){
                    end= totalCount;
                }
                
                if (start >= end) {
                    return null;
                }
                if(sort==null){
                    topDocs = indexSearcher.search(query, end);
                }else{
                    topDocs = indexSearcher.search(query, end,sort);
                }
                hits=new ScoreDoc[end-start];
                
                for(int i=start;i<end;i++){
                    hits[i-start]=topDocs.scoreDocs[i];
                }
                
            }
            if (hits == null || hits.length < 1) {
                return null;
            }
    
            list = new ArrayList<>(hits.length);
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = indexSearcher.doc(hits[i].doc);
                T t = clazz.newInstance();
                document2Bean(hitDoc, t);
                list.add(t);
            }
            
          }catch(Exception e){
              logger.error(e.getMessage(),e);
          }finally{
              if(indexReader!=null){
                  indexReader.close();
              }
              if(directory!=null){
                  directory.close();
              }
            
          }
        return list;
    }
    
	
	
	/**
	 * 获取索引的目录
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static File getIndexDirFile(String rootdir,Class clazz) {
		if (clazz == null) {
			return null;
		}
		File file = new File(rootdir + "/" + clazz.getName());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;

	}

	/**
	 * 获取实体类的索引文档
	 * 
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Directory getDirectory(String rootdir,Class clazz) throws IOException {
		File indexDirFile = getIndexDirFile(rootdir,clazz);
		if (indexDirFile == null) {
			return null;
		}
		Path indexDirPath = indexDirFile.toPath();
		// 索引不可读
		if (!Files.isReadable(indexDirPath)) {
			return null;
		}
		// 获取索引目录文件
		Directory directory = FSDirectory.open(indexDirPath);
		return directory;

	}
	
   
    
    /**
     * 索引文档转化为bean
     * @param document
     * @param t
     * @return
     * @throws Exception
     */
    private static Object  document2Bean(Document document, Object t) throws Exception{
        
        if(document==null||t==null){
            return null;
        }
        
        List<FieldInfo> luceneFields = ClassUtils.getLuceneFields(t.getClass());
        
        if(CollectionUtils.isEmpty(luceneFields)){
            return null;
        }
        for (FieldInfo finfo : luceneFields) {
            String fieldName=finfo.getFieldName();
            
            String fieldValue = document.get(fieldName);
            
            Class fieldType =finfo.getFieldType();
            if(Integer.class==fieldType||int.class==fieldType){//数字
                ClassUtils.setPropertieValue(fieldName, t, Integer.valueOf(fieldValue));
            }else if(BigInteger.class==fieldType){//数字
                ClassUtils.setPropertieValue(fieldName, t, new BigInteger(fieldValue));
            }else if(Long.class==fieldType||long.class==fieldType){//数字
                ClassUtils.setPropertieValue(fieldName, t, Long.valueOf(fieldValue));
            }else if(Float.class==fieldType||float.class==fieldType){//数字
                ClassUtils.setPropertieValue(fieldName, t, Float.valueOf(fieldValue));
            }else if(Double.class==fieldType||double.class==fieldType){//数字
                ClassUtils.setPropertieValue(fieldName, t, Double.valueOf(fieldValue));
            }else if(BigDecimal.class==fieldType){//进行存储和索引,不进行分词引
                ClassUtils.setPropertieValue(fieldName, t, new BigDecimal(fieldValue));
            }else if(BigInteger.class==fieldType){//进行存储和索引,不进行分词引
                ClassUtils.setPropertieValue(fieldName, t, new BigInteger(fieldValue));
            }else if(Date.class==fieldType){//日期
              //  ClassUtils.setPropertieValue(fieldName, t, DateUtils.convertString2Date(DateUtils.DEFAILT_DATE_TIME_PATTERN, fieldValue.toString()));
                ClassUtils.setPropertieValue(fieldName, t, new Date(Long.valueOf(fieldValue)));
            }else{
                ClassUtils.setPropertieValue(fieldName, t, fieldValue);
            }
        }
        
        return t;
        
    }
    
    
    
    /**
     * bean转化为索引文档
     * @param entity
     * @return
     * @throws Exception
     */
    private static Document bean2Document(Object entity) throws Exception{
     // 获取索引的字段,为null则不进行保存
        List<FieldInfo> luceneFields = ClassUtils.getLuceneFields(entity.getClass());
        if (CollectionUtils.isEmpty(luceneFields)) {
            return null;
        }

        Document doc = new Document();
        for (FieldInfo finfo : luceneFields) {
            
            String fieldName=finfo.getFieldName();
            Class fieldType=finfo.getFieldType();
            Object _obj = ClassUtils.getPropertieValue(fieldName, entity);
            if (_obj == null||StringUtils.isBlank(_obj.toString())) {
                continue;
            }
            String _value = _obj.toString();
            
           
            if(Integer.class==fieldType||int.class==fieldType){//数字进行存储和索引,不进行分词
                Integer value=Integer.valueOf(_value);
                
                doc.add(new StoredField(fieldName, value));
                doc.add(new IntPoint(fieldName,value));
                doc.add(new NumericDocValuesField(fieldName, value));
                
            }else if(Long.class==fieldType||long.class==fieldType){//数字进行存储和索引,不进行分词
                Long value=Long.valueOf(_value);
                
                doc.add(new StoredField(fieldName, value));
                doc.add(new LongPoint(fieldName,  value));
                doc.add(new NumericDocValuesField(fieldName, value));
            }else if(Float.class==fieldType||float.class==fieldType){//数字进行存储和索引,不进行分词
                Float value=Float.valueOf(_value);
                
                doc.add(new StoredField(fieldName, value));
                doc.add(new FloatPoint(fieldName,  value));
                doc.add(new FloatDocValuesField(fieldName, value));
            }else if(Double.class==fieldType||double.class==fieldType){//数字进行存储和索引,不进行分词
                Double value=Double.valueOf(_value);
                
                doc.add(new StoredField(fieldName, value));
                doc.add(new DoublePoint(fieldName, value));
                doc.add(new DoubleDocValuesField(fieldName, value));
                
            }else if(Date.class==fieldType){//数字进行存储和索引,不进行分词
            // _field=new StringField(fieldName, DateUtils.convertDate2String(DateUtils.DEFAILT_DATE_TIME_PATTERN,(Date)_obj), Store.YES);
                Long value=((Date)_obj).getTime();
                doc.add(new StoredField(fieldName, value));
                doc.add(new LongPoint(fieldName,  value));
                doc.add(new NumericDocValuesField(fieldName, value));
            }else if(BigInteger.class==fieldType){//数字
                doc.add(new StringField(fieldName, _value, Store.YES));
                doc.add(new NumericDocValuesField(fieldName,Long.valueOf(_value)));
            }else if(BigDecimal.class==fieldType){//进行存储和索引,不进行分词引
                doc.add(new StringField(fieldName, _value, Store.YES));
            }else if(finfo.getPk()){//如果是主键,进行存储和索引,不进行分词引
                doc.add(new StringField(fieldName, _value, Store.YES));
            }else{
                doc.add(new TextField(fieldName, _value, Store.YES));
         }
                //_field = new Field(fieldName, _value, TextField.TYPE_STORED);
            
        }
        
        return doc;
        
        
    }
    
    
    


}