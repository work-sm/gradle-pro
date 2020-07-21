package com.sam.demo.process1.exec;

import com.sam.demo.process1.AbstractProcessor;
import com.sam.demo.process1.Processor;
import com.sam.demo.process1.util.JsonUtils;
import com.sam.demo.process1.work.SimpleProductLine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AscNodeCompt_exe extends AbstractProcessor {

    private RandomAccessFile raf;
    private RandomAccessFile bfr;

    public AscNodeCompt_exe(String exeFullName) throws IOException {
        super(exeFullName);
        File paramHome = new File(home().getParent(), "/params/input");
        File resultHome = new File(home().getParent(), "/params/output");
        File tle = new File(paramHome, "satPara_opData.json");
        String name = JsonUtils.getVal(tle, "$.name");
        File kepl = new File(resultHome, "ascNodeData_" + name + ".txt");
        if (!tle.exists() || tle.isDirectory()) {
            tle.delete();
            tle.createNewFile();
        }
        if (!kepl.exists() || kepl.isDirectory()) {
            kepl.delete();
            kepl.createNewFile();
        }
        sign("CALCULATION COMPLETED");
        raf = new RandomAccessFile(tle, "rw");
        bfr = new RandomAccessFile(kepl, "r");
    }

    @Override
    protected long waitTime() {
        return 1000;
    }

    @Override
    protected void next(String params) throws Exception {
        raf.setLength(0);
        raf.seek(0);
        raf.write(params.getBytes());
    }

    @Override
    protected String completed() throws Exception {
        bfr.seek(0);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = bfr.readLine()) != null) {
            byte[] bytes = getBytes(line.toCharArray());
            sb.append(new String(bytes)).append("\n");
        }
        return sb.toString();
    }

    private byte[] getBytes(char[] chars) {
        byte[] result = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            result[i] = (byte) chars[i];
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        String param = "{\n" +
                "\t\"dataDir\": \"../data/hpop\",\n" +
                "    \"name\": \"鸿雁0101\",\n" +
                "\t\"orbPlaneNo\": 1,\n" +
                "\t\"orbInPlaneNo\": 1,\n" +
                "    \"loadExternalData\": false,\t\n" +
                "\t\"opDataPath\": \"../data/outData/hpopOpData_鸿雁0101.txt\",  \n" +
                "\t\"orbEleFile\": \"../data/outData/OrbEleData_鸿雁0101.txt\",\n" +
                "\t\"orbEle\": {\n" +
                "        \"epoch\": \"2020-07-10 00:00:00.000\",\n" +
                "\t\t\"semimajorAxis\": 6939.854776,\n" +
                "\t\t\"eccentricity\": 0.00220694,\n" +
                "\t\t\"inclination\": 44.85626258,\n" +
                "\t\t\"argPerigee\": 67.94041898,\n" +
                "\t\t\"raan\": 279.70690422,\n" +
                "\t\t\"meanAnomaly\": 206.85825969\n" +
                "    },\n" +
                "\t\"forceModel\": {\n" +
                "\t\t\"mass\": 66.5,\n" +
                "\t\t\"dragArea\": 0.3,\n" +
                "\t\t\"Cd\": 2.3,\n" +
                "\t\t\"SPArea\": 0.6,\n" +
                "\t\t\"Cr\": 1.24,\n" +
                "\t\t\"atmModel\": 5,\n" +
                "\t\t\"SPMode\": 2,\n" +
                "\t\t\"order\": 70,\n" +
                "\t\t\"solidTides\": true,\n" +
                "\t\t\"oceanTides\": true,\n" +
                "\t\t\"atmTides\": true,\n" +
                "\t\t\"thirdBodyGravity\": [\n" +
                "\t\t\t\t\t\t\ttrue,\n" +
                "\t\t\t\t\t\t\ttrue,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse,\n" +
                "\t\t\t\t\t\t\tfalse\n" +
                "\t\t],\n" +
                "\t\t\"dayF10.7\": 100.9,\n" +
                "\t\t\"avgF10.7\": 100.5,\n" +
                "\t\t\"KP\": 2.0,\n" +
                "\t\t\"APFile\": \"\",\n" +
                "\t\t\"KPFile\": \"\"\n" +
                "\t} \n" +
                "}\n";
        SimpleProductLine simpleProductLine = new SimpleProductLine();
        ExecutorService service = Executors.newFixedThreadPool(3);
        Processor processor1 = new AscNodeCompt_exe("C:\\Users\\Administrator\\Desktop\\runtime\\software1\\bin\\ascNodeCompt.exe");
        Processor processor2 = new AscNodeCompt_exe("C:\\Users\\Administrator\\Desktop\\runtime\\software2\\bin\\ascNodeCompt.exe");
        Processor processor3 = new AscNodeCompt_exe("C:\\Users\\Administrator\\Desktop\\runtime\\software3\\bin\\ascNodeCompt.exe");
        processor1.consume(simpleProductLine);
        processor2.consume(simpleProductLine);
        processor3.consume(simpleProductLine);
        service.execute(processor1);
        service.execute(processor2);
        service.execute(processor3);
        service.shutdown();

        processor1.produce(param);
        processor2.produce(param);
        processor3.produce(param);
    }

}
