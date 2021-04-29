import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.Chapter;
import com.jk.data.mybatis.bean.zjChapter;
import com.jk.data.mybatis.dao.ChapterDao;
import com.jk.data.mybatis.dao.Chapter_zujianDao;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by 76204 on 2017/7/15.
 */
public class ChapterTranse {
    private static Logger logger = Logger.getLogger(ChapterTranse.class);
    public static void main(String[] args) {
        Chapter_zujianDao chapter_zujuanDao = AppUtils.daoFactory(Chapter_zujianDao.class);
        ChapterDao chapterDao = AppUtils.daoFactory(ChapterDao.class);
        List<zjChapter> all = chapter_zujuanDao.getAll();
        for (zjChapter chapter_zujuan : all) {
            String id = chapter_zujuan.getId();
            String relateBookId = chapter_zujuan.getRelateBookId();
            String name = chapter_zujuan.getName();
            String pid = chapter_zujuan.getPid();
            Integer knowledgeTag = chapter_zujuan.getKnowledgeTag();
            Integer sortNum = chapter_zujuan.getSortNum();

            StringBuilder   id1=new StringBuilder();
             String  relateBookId1=null;
             String  name1=null;
             Integer level=0;
             String  pk=null;
            Integer   knowledgeTag1=null;


            id1.insert(0,String.format("%02d.",chapter_zujuan.getSortNum()));

            if(pid==null){
                level=1;
            }else{

                zjChapter chapter2 = chapter_zujuanDao.getByPid(pid);
                id1.insert(0,String.format("%02d.",chapter2.getSortNum()));

                Integer sortNum2 = chapter2.getSortNum();
                String pid2 = chapter2.getPid();
                if(pid2==null){
                    level=2;
                }else{
                    zjChapter chapter3 = chapter_zujuanDao.getByPid(pid2);
                    id1.insert(0,String.format("%02d.",chapter3.getSortNum()));
                    String pid3 = chapter3.getPid();
                    if(pid3==null){
                        level=3;
                    }else{
                        zjChapter chapter4 = chapter_zujuanDao.getByPid(pid3);
                        id1.insert(0,String.format("%02d.",chapter4.getSortNum()));
                        String pid4 = chapter4.getPid();
                        if(pid4==null){
                            level=4;
                        }
                    }
                }
            }
            Chapter chapter = new Chapter();
            chapter.setKnowledgeTag(knowledgeTag);
            chapter.setId(id1.insert(0,"G").substring(0,id1.length()-1));
            chapter.setRelateBookId(relateBookId.replaceAll("([B-Z])","|$1"));
            chapter.setName(name);
            chapter.setLevel(level);
            chapter.setPk(id);

//            System.out.println(relateBookId.replaceAll("([B-Z])","|$1")+"-----");
            if(relateBookId!=null || !relateBookId.isEmpty()){
                chapterDao.add(chapter);
                logger.info("-------入库---------"+id);

                logger.info(chapter);
            }else{
                logger.info("------漏掉------"+id);
            }


        }

    }
}
