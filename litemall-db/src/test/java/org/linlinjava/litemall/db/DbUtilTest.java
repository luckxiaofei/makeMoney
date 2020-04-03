package org.linlinjava.litemall.db;

import org.junit.Test;
import org.linlinjava.litemall.db.util.DbUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DbUtilTest {
    @Test
    public void testBackup() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        String localTime = df.format(time);


        System.out.println(localTime);


    }

    //    这个测试用例会重置litemall数据库，所以比较危险，请开发者注意
//    @Test
    public void testLoad() {
        File file = new File("test.sql");
        DbUtil.load(file, "litemall", "litemall123456", "litemall");
    }
}
