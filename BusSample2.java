package Bus2;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BusSample2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 파일 객체 생성
		Path path = Paths.get("./INFILE2/busInfos.txt");
		Path stationPath = Paths.get("./INFILE2/stationInfo.txt");
		// 캐릭터셋 지정
		Charset cs = StandardCharsets.UTF_8;
		// 파일 내용담을 리스트
		List<String> list = new ArrayList<String>();
		List<String> stationList = new ArrayList<String>();
		try {
			list = Files.readAllLines(path, cs);
			stationList = Files.readAllLines(stationPath, cs);
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

			BusInfo busInfo = new BusInfo(i, busTime, busName, busLocation);
			busInfoList.add(busInfo);
		}
		busInfoListOri.addAll(busInfoList);
		Collections.sort(busInfoList, new BusInfoCompare());

		List<BusInfo> busInfoListIndex = new ArrayList<>();
		for(int i = 0; i < busInfoList.size(); i++) {
			BusInfo busInfo = new BusInfo(i, busInfoList.get(i).busTime, busInfoList.get(i).busName, busInfoList.get(i).busLocation);
			busInfoListIndex.add(busInfo);
		}
		
//		Station01#400
//		Station02#1000
//		Station03#3000
//		Station04#9000
		List<BusStationInfo> busStationInfoList = new ArrayList<>();
		String busStationName;
		int busStationLocation;
		for(int i = 0; i < stationList.size(); i++) {
			if(stationList.get(i).equals("PRINT")) {
				break;
			}
			else {
				busStationName = stationList.get(i).split("#")[0];
				busStationLocation = Integer.parseInt(stationList.get(i).split("#")[1]);
				
				BusStationInfo busStation = new BusStationInfo(i, busStationName, busStationLocation);
				busStationInfoList.add(busStation);
			}
		}

		System.out.println();
		
		// logic1
		String writeValue = "";
		String busTimeWrite;
		String busNameWrite;
		String busNameHuWrite;
		String busNameSunWrite;
		int busLocationHuWrite;
		int busLocationSunWrite;
		
		// 후행이 없는경우 10:03:00#BUS02#NOBUS,00000#BUS01,01758
		// 선행이 없는경우 10:03:00#BUS03#BUS01,04950#NOBUS,00000
		// 선행 후행 모두 있는경우 10:03:00#BUS01#BUS02,01758#BUS03,04950
		
		for(int i = 0; i < busInfoListOri.size(); i++) {
			for(int j = 0; j < busInfoListIndex.size(); j++) {
				if(busInfoListOri.get(i).busName.equals(busInfoListIndex.get(j).busName)) {
					if(busInfoListIndex.get(j).index == 0) {
						busTimeWrite = busInfoListOri.get(i).busTime;
						busNameWrite = busInfoListOri.get(i).busName;
						// 후행 -1
						busNameHuWrite = "NOBUS";
						busLocationHuWrite = 000000;
						// 선행 +1
						busNameSunWrite = busInfoListIndex.get(j+1).busName;
						busLocationSunWrite = busInfoListIndex.get(j+1).busLocation - busInfoListIndex.get(j).busLocation;
						
						writeValue = busTimeWrite + "#" + busNameWrite + "#" + busNameHuWrite + "," + String.format("%05d", busLocationHuWrite) + "#" + busNameSunWrite + "," + String.format("%05d", busLocationSunWrite);
						fileWrite(writeValue);
					}
					else if(busInfoListIndex.get(j).index == busInfoListIndex.size() - 1) {
						busTimeWrite = busInfoListOri.get(i).busTime;
						busNameWrite = busInfoListOri.get(i).busName;
						// 후행 -1
						busNameHuWrite = busInfoListIndex.get(j-1).busName;
						busLocationHuWrite = busInfoListIndex.get(j).busLocation - busInfoListIndex.get(j-1).busLocation;

						// 선행 +1
						busNameSunWrite = "NOBUS";
						busLocationSunWrite = 000000;
						
						writeValue = busTimeWrite + "#" + busNameWrite + "#" + busNameHuWrite + "," + String.format("%05d", busLocationHuWrite) + "#" + busNameSunWrite + "," + String.format("%05d", busLocationSunWrite);
						fileWrite(writeValue);
					}
					else {
						busTimeWrite = busInfoListOri.get(i).busTime;
						busNameWrite = busInfoListOri.get(i).busName;
						// 후행 -1
						busNameHuWrite = busInfoListIndex.get(j-1).busName;
						busLocationHuWrite = busInfoListIndex.get(j).busLocation - busInfoListIndex.get(j-1).busLocation;

						// 선행 +1
						busNameSunWrite = busInfoListIndex.get(j+1).busName;
						busLocationSunWrite = busInfoListIndex.get(j+1).busLocation - busInfoListIndex.get(j).busLocation;
						
						writeValue = busTimeWrite + "#" + busNameWrite + "#" + busNameHuWrite + "," + String.format("%05d", busLocationHuWrite) + "#" + busNameSunWrite + "," + String.format("%05d", busLocationSunWrite);
						fileWrite(writeValue);
					}
				}
			}
			
		}
		
		// logic 2
//		Station01#NOBUS,00000
//		Station02#BUS02,00185
//		Station03#BUS01,00427
//		Station04#BUS04,00277
		
		String busStationNameWrite;
		String busNameWrite2;
		int busStationLocationWrite;
		String writeValue2;
		Map<String, Integer> map = null;
		ValueComparator bvc;
		TreeMap<String, Integer> sorted_map = null;
		
		for(int i = 0; i < busStationInfoList.size(); i++) {
			map = new HashMap<>();
			bvc = new ValueComparator(map);
			sorted_map = new TreeMap<String, Integer>(bvc);
			
			for(int j = 0; j < busInfoListIndex.size(); j++) {
				if(busStationInfoList.get(i).busStationLocation > busInfoListIndex.get(j).busLocation) {
					map.put(busInfoListIndex.get(j).busName, busInfoListIndex.get(j).busLocation);
				}
			}
			
			sorted_map.putAll(map);
			
			if(sorted_map.isEmpty()) {
				busStationNameWrite = busStationInfoList.get(i).busStationName;
				busNameWrite2 = "NOBUS";
				busStationLocationWrite = 0;
				
				writeValue2 = busStationNameWrite + "#" + busNameWrite2 + "," + String.format("%05d", busStationLocationWrite);
				fileWrite1(writeValue2);
			}
			else {
				busStationNameWrite = busStationInfoList.get(i).busStationName;
				
				String busNametmp = null;
				int busLocationtmp = 0;
				for(String key : sorted_map.keySet()) {
					busNametmp = key;
					busLocationtmp = map.get(key);
					break;
				}
				
				busNameWrite2 = busNametmp;
				busStationLocationWrite = busStationInfoList.get(i).busStationLocation - busLocationtmp;
				
				writeValue2 = busStationNameWrite + "#" + busNameWrite2 + "," + String.format("%05d", busStationLocationWrite);
				fileWrite1(writeValue2);
			}
		}

	}

	public static void fileWrite(String value) {
		try {
			// 파일 객체 생성
			File file = new File("./OUTFILE2/outfile3.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));

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
	
	public static void fileWrite1(String value) {
		try {
			// 파일 객체 생성
			File file = new File("./OUTFILE2/outfile4.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));

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
	int index;
	String busTime;
	String busName;
	int busLocation;

	BusInfo(int index, String busTime, String busName, int busLocation) {
		this.index = index;
		this.busTime = busTime;
		this.busName = busName;
		this.busLocation = busLocation;
	}
}

class BusStationInfo {
	int index;
	String busStationName;
	int busStationLocation;

	BusStationInfo(int index, String busStationName, int busStationLocation) {
		this.index = index;
		this.busStationName = busStationName;
		this.busStationLocation = busStationLocation;
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

class ValueComparator implements Comparator<String> {
	 
    Map<String, Integer> base;
     
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }
 
    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) { //반대로 하면 오름차순 <=
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}