package test.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import util.ConsoleUtil;

public class ConsoleUtilTest {
    
    @Test
    public void getDisplayWidthTest() {
        assertEquals(13,ConsoleUtil.getDisplayWidth("（hello123!）"));
        assertEquals(11,ConsoleUtil.getDisplayWidth("(hello123!)"));
        assertEquals(6,ConsoleUtil.getDisplayWidth("やあ！"));
        assertEquals(8, ConsoleUtil.getDisplayWidth("aハローb"));
        assertEquals(8, ConsoleUtil.getDisplayWidth("aハローb"));
        assertEquals(20, ConsoleUtil.getDisplayWidth("０１２３４５６７８９"));
    }
}
