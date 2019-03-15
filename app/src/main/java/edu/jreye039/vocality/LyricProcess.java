package edu.jreye039.vocality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LyricProcess {
    private List<LyricInfo> lrcList;	//List集合存放歌词内容对象
    private LyricInfo mLrcContent;		//声明一个歌词内容对象
    /**
     * 无参构造函数用来实例化对象
     */
    public LyricProcess() {
        mLrcContent = new LyricInfo();
        lrcList = new ArrayList<LyricInfo>();
    }

    /**
     * 读取歌词
     * @param
     * @return
     */
    // TODO make sure the lyric can be get from server.  应该要获取MP3的内容来知道是啥歌！！！
    public String readLRC( String lyric) {
        //定义一个StringBuilder对象，用来存放歌词内容
        StringBuilder stringBuilder = new StringBuilder();
        String[] ss = lyric.split("\n");
        for (int i = 0; i < ss.length; i++) {
            if(ss[i].length()>1){
                String splitLrcData[] = ss[i].split("@");
                mLrcContent.setLrcStr(splitLrcData[1]);

                //处理歌词取得歌曲的时间
                int lrcTime = time2Str(splitLrcData[0]);

                mLrcContent.setLrcTime(lrcTime);

                //添加进列表数组
                lrcList.add(mLrcContent);

                //新创建歌词内容对象
                mLrcContent = new LyricInfo();
            }
        }

        return stringBuilder.toString();
    }
    /**
     * 解析歌词时间
     * 歌词内容格式如下：
     * [00:02.32]陈奕迅
     * [00:03.43]好久不见
     * [00:05.22]歌词制作  王涛
     * @param timeStr
     * @return
     */
    public int time2Str(String timeStr) {
        timeStr = timeStr.replace(":", ".");
        timeStr = timeStr.replace(".", "ಠ౪ಠ");

        String timeData[] = timeStr.split("ಠ౪ಠ");	//将时间分隔成字符串数组

        //分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);

        //计算上一行与下一行的时间转换为毫秒数
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        return currentTime;
    }
    public List<LyricInfo> getLrcList() {
        return lrcList;
    }
}
