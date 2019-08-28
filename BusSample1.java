package Bus1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BusSample1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 파일 객체 생성
		Path path = Paths.get("./INFILE1/busInfo.txt");
		// 캐릭터셋 지정
		Charset cs = StandardCharsets.UTF_8;
		// 파일 내용담을 리스트
		List<String> list = new ArrayList<String>();
		try {
			list = Files.readAllLines(path, cs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String lastBusInfo = "";
		String busTime;
		String busName;
		int busLocation;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals("PRINT")) {
				lastBusInfo = list.get(i - 1);
			}
		}
		// System.out.println(busTime);
		busTime = lastBusInfo.split("#")[0];

		List<BusInfo> busInfoList = new ArrayList<>();
		List<BusInfo> busInfoListOri = new ArrayList<>();

		for (int i = 1; i < lastBusInfo.split("#").length; i++) {
			busName = lastBusInfo.split("#")[i].split(",")[0];
			busLocation = Integer.parseInt(lastBusInfo.split("#")[i].split(",")[1]);

			BusInfo busInfo = new BusInfo(busTime, busName, busLocation);
			busInfoList.add(busInfo);
		}
		busInfoListOri.addAll(busInfoList);
		Collections.sort(busInfoList, new BusInfoCompare());

		System.out.println();

		// logic
		String writeValue = "";
		String busTimeWrite;
		String busNameWrite;
		int busLocationWrite;
		
		for(int i = 0; i < busInfoList.size(); i++) {
			if(i == 0) {
				// 후행이 없는경우 10:03:00#BUS02#NOBUS,00000#BUS01,01758
				
			}
			else if(i == busInfoList.size() - 1) {
				// 선행이 없는경우 10:03:00#BUS03#BUS01,04950#NOBUS,00000
			}
			else {
				// 선행 후행 모두 있는경우 10:03:00#BUS01#BUS02,01758#BUS03,04950
			}
		}

	}

	public void fileWrite(String value) {
		try {
			// 파일 객체 생성
			File file = new File("./OUTFILE1/outfile2.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

			if (file.isFile() && file.canWrite()) {
				// 쓰기
				bufferedWriter.write(value);
				// 개행문자쓰기
				bufferedWriter.newLine();

				bufferedWriter.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}

class BusInfo {
	String busTime;
	String busName;
	int busLocation;

	BusInfo(String busTime, String busName, int busLocation) {
		this.busTime = busTime;
		this.busName = busName;
		this.busLocation = busLocation;
	}
}

class BusInfoCompare implements Comparator<BusInfo> {
	int ret = 0;

	@Override
	public int compare(BusInfo s1, BusInfo s2) {
		if (s1.busLocation < s2.busLocation) {
			ret = -1;
		}
		if (s1.busLocation > s2.busLocation) {
			ret = 1;
		}
		return ret;
	}
}