import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.Chapter;
import com.jk.data.mybatis.bean.Chapter_zujuan;
import com.jk.data.mybatis.dao.Chapter_zujianDao;

import java.util.List;

/**
 * Created by 76204 on 2017/7/15.
 */
public class ChapterTranse {
    public static void main(String[] args) {
        Chapter_zujianDao chapterDao = AppUtils.daoFactory(Chapter_zujianDao.class);
        List<Chapter_zujuan> all = chapterDao.getAll();
        for (Chapter_zujuan chapter_zujuan : all) {
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

                Chapter_zujuan chapter2 = chapterDao.getByPid(pid);
                id1.insert(0,String.format("%02d.",chapter2.getSortNum()));

                Integer sortNum2 = chapter2.getSortNum();
                String pid2 = chapter2.getPid();
                if(pid2==null){
                    level=2;
                }else{
                    Chapter_zujuan chapter3 = chapterDao.getByPid(pid2);
                    id1.insert(0,String.format("%02d.",chapter3.getSortNum()));
                    String pid3 = chapter3.getPid();
                    if(pid3==null){
                        level=3;
                    }else{
                        Chapter_zujuan chapter4 = chapterDao.getByPid(pid3);
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
            chapter.setRelateBookId(relateBookId);
            chapter.setName(name);
            chapter.setLevel(level);
            chapter.setPk(id);

            System.out.println(chapter_zujuan+"----------");


        }

    }
}
