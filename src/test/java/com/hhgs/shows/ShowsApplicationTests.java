package com.hhgs.shows;

import com.hhgs.shows.mapper.PlantShowsMapper;
import com.hhgs.shows.model.BasePoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShowsApplicationTests {

	@Autowired
	PlantShowsMapper plantShowsMapper;

	@Test
	public void contextLoads() {
		//string basepath = request.getscheme()+"://"+request.getservername()+":"+request.getserverport()+path+"/";

		Date dnow = new Date(); //当前时间

		Date dbefore = new Date();

		Calendar calendar = Calendar.getInstance(); //得到日历

		calendar.setTime(dnow);//把当前时间赋给日历

		calendar.add(Calendar.DAY_OF_MONTH, -1);//设置为前一天

		dbefore = calendar.getTime();//得到前一天的时间

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式

		String defaultstartdate = sdf.format(dbefore);//格式化前一天

		String defaultenddate = sdf.format(dnow); //格式化当前时间

		System.out.println("前一天的时间是：" + defaultstartdate);

		System.out.println("生成的时间是：" + defaultenddate);
	}

	@Test
	public void testQueryPoints() {
		List<BasePoints> basePoints = plantShowsMapper.queryAllPoints();

		Map<Integer, List<BasePoints>> pointsMap = basePoints.stream().collect(Collectors.groupingBy(BasePoints::getPlantCode));

		pointsMap.forEach((plantCode,pointsList)-> {
			System.out.println(plantCode+"==========="+pointsList);
		});
	}

}
