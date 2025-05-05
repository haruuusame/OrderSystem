package test.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import util.ConsoleUtil;

public class ConsoleUtilTest {
    
    @Test
    public void getDisplayWidthTest() {
        assertEquals(13,ConsoleUtil.getLineLength("（hello123!）"));
        assertEquals(11,ConsoleUtil.getLineLength("(hello123!)"));
        assertEquals(6,ConsoleUtil.getLineLength("やあ！"));
        assertEquals(8, ConsoleUtil.getLineLength("aハローb"));
        assertEquals(8, ConsoleUtil.getLineLength("aハローb"));
        assertEquals(20, ConsoleUtil.getLineLength("０１２３４５６７８９"));
    }

    @Test
    public void wrapLineTest() {
        int maxWidth = 10;
        String line = "helloworld";
        List<String> strlist = ConsoleUtil.wrapLine(line,10);
        assertEquals(1,strlist.size());
        assertEquals("helloworld",strlist.get(0));
        line += "1";
        strlist = ConsoleUtil.wrapLine(line,maxWidth);
        assertEquals(2,strlist.size());
        assertEquals("helloworld",strlist.get(0));
        assertEquals("1",strlist.get(1));
        line = "あいうえおかきくけこさshiすseそ";
        strlist = ConsoleUtil.wrapLine(line,maxWidth);
        assertEquals(4,strlist.size());
        assertEquals("あいうえお",strlist.get(0));
        assertEquals("かきくけこ",strlist.get(1));
        assertEquals("さshiすse",strlist.get(2));
        assertEquals("そ",strlist.get(3));
    }

}
