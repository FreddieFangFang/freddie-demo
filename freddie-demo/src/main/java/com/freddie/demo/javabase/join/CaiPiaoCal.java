package com.freddie.demo.javabase.join;

import java.util.*;

/**
 * 彩票算法
 * Created by jesonking
 * 双色球 6点钟方向出球
 * 大乐透 12点方向
 * 功能1：普通生成号码预测，并给出高频号码
 * 1、参数：彩票类型，输出次数，高频红球输出个数，高频蓝球输出个数
 * 功能2：根据当期开奖号码，预测下次号码。若最高100万次还未匹配到中奖号码则自动给出一组号码
 * 2、参数：当期开奖号码，匹配次数：10000,100000,1000000
 */
public class CaiPiaoCal {
    public static void main(String[] args) {
//        printfCP(1, 2000);
        printfCP(2, 2);
        //根据当前号码预测下一次号码
        String currentCaipiao = "06,08,16,34,35,05,06";
        //String currentCaipiao = "07,08,13,19,29,11,12";
        String[] strs = currentCaipiao.split(",");
//        List list= Arrays.asList(strs);
//        String logcaipiao = getOneCPByLot(2,10000);
//        System.out.println(logcaipiao);
//        logcaipiao = getOneCPByLot(1,100);
        //System.out.println("双色球："+nextCaipiao(logcaipiao,2,1000000) + ",高频彩票：" + logcaipiao);
        System.out.println("大乐透：" + nextCaipiao(currentCaipiao, 2, 10000));
        //nextCaipiao(logcaipiao.substring(1,logcaipiao.length()-1),1,1000000,6);
//        String currentCaipiao = "06,08,09,13,19,22,10";
//        nextCaipiao(currentCaipiao,1,500000,6);
//        System.out.println(logcaipiao);

//        getOftenAppearCaipiao(1,20,100000,6);
    }

    /**
     * 通过高频数字组合获取一组号码，规则是
     * 3,2,1. 前5个高频中选3个，前6到10中选2个，11-15以后的选一个
     * 大乐透是3,1,1
     * 蓝球：从前5个中选一个或者2个
     *
     * @param type     1:双色球  2：大乐透
     * @param execTime 执行多少次，进行计算输出
     * @return
     */
    private static String getOneCPByLot(int type, int execTime) {
        List<String> resultList = new ArrayList<>();
        int redNum = 33;
        int blueNum = 16;
        if (type == 2) {
            redNum = 35;
            blueNum = 12;
        }
        Map<String, String> lotMap = printfCP(type, execTime, redNum, blueNum);
        List<String> redList = new ArrayList<>();
        String redStr = lotMap.get("redLotStr");
        redStr = redStr.substring(1, redStr.length() - 1);
        String blueStr = lotMap.get("blueLotStr");
        blueStr = blueStr.substring(1, blueStr.length() - 1);
        String[] redArr = redStr.split(",");
        //获取前三个
        List<Integer> list12 = getRandomNumList(3, 0, 5);
        redList.add(redArr[list12.get(0)].trim());
        redList.add(redArr[list12.get(1)].trim());
        redList.add(redArr[list12.get(2)].trim());
        //获取前2个或1个
        List<Integer> list24 = null;
        if (type == 1) {
            list24 = getRandomNumList(2, 5, 10);
            redList.add(redArr[list24.get(0)].trim());
            redList.add(redArr[list24.get(1)].trim());
        } else {
            list24 = getRandomNumList(1, 5, 10);
            redList.add(redArr[list24.get(0)].trim());
        }
        //最后一个
        List<Integer> list24Over = getRandomNumList(1, 10, 15);
        redList.add(redArr[list24Over.get(0)].trim());
        Collections.sort(redList);
        resultList.addAll(redList);
        //获取蓝球
        List<String> blueList = new ArrayList<>();
        String[] blueArr = blueStr.split(",");
        List<Integer> list12Or16 = null;
        if (type == 1) {
            list12Or16 = getRandomNumList(1, 0, 5);
            blueList.add(blueArr[list12Or16.get(0)].trim());
        } else {
            list12Or16 = getRandomNumList(2, 0, 5);
            blueList.add(blueArr[list12Or16.get(0)].trim());
            blueList.add(blueArr[list12Or16.get(1)].trim());
        }
        Collections.sort(blueList);
        resultList.addAll(blueList);
        return resultList.toString();
    }

    /**
     * 默认
     * 随机打印多少组彩票
     *
     * @param type 1:双色球  2：大乐透
     * @param num  打印次数
     */
    private static Map<String, String> printfCP(int type, int num) {
        return printfCP(type, num, 10, 5);
    }

    /**
     * 随机打印多少组彩票
     *
     * @param type    1:双色球  2：大乐透
     * @param num     打印次数
     * @param redNum  红球高频输出次数  redLotStr
     * @param blueNum 蓝球高频输出次数 blueLotStr
     */
    private static Map<String, String> printfCP(int type, int num, int redNum, int blueNum) {
        List<String> lotRedList = new ArrayList<>();//存放红球
        List<String> lotLanList = new ArrayList<>();//存放蓝球
        List<String> ssqList = null;
        List<String> dltList = null;
        for (int i = 0; i < num; i++) {
            if (type == 1) {
                ssqList = shuangSeQiu();
                autoFenPei(ssqList, lotRedList, lotLanList, type);
                System.out.println("双色球" + (i + 1) + ":" + ssqList.toString());
            } else {
                dltList = daLeTou();
                autoFenPei(dltList, lotRedList, lotLanList, type);
                System.out.println("大乐透" + (i + 1) + ":" + dltList.toString());
            }
        }
        List<String> redNumList = handlerLotNum(lotRedList, redNum);
        List<String> blueNumList = handlerLotNum(lotLanList, blueNum);
        System.out.println("出现的高频红球数字：" + redNumList.toString());
        System.out.println("出现的高频蓝球数字：" + blueNumList.toString());
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("redLotStr", redNumList.toString());
        resultMap.put("blueLotStr", blueNumList.toString());
        return resultMap;
    }

    /**
     * 自动红球和篮球分到对应的集合中
     *
     * @param qiuList
     * @param type
     * @return
     */
    private static void autoFenPei(List<String> qiuList, List<String> redList, List<String> blueList, int type) {
        redList.add(qiuList.get(0));
        redList.add(qiuList.get(1));
        redList.add(qiuList.get(2));
        redList.add(qiuList.get(3));
        redList.add(qiuList.get(4));
        if (type == 1) {
            redList.add(qiuList.get(5));
        } else {
            blueList.add(qiuList.get(5));
        }
        blueList.add(qiuList.get(6));
    }

    public static String nextCaipiao(String currentCaipiao, int type) {
        int execTime = 1000000;
        return nextCaipiao(currentCaipiao, type, execTime);
    }

    /**
     * 获取随机中最快出现的一组数据
     *
     * @param type     彩票类型
     * @param forNum   循环次数
     * @param execTime 检测循环次数
     * @param num      匹配几个号码
     * @return 返回最快出现的一组号码
     */
    public static String getOftenAppearCaipiao(int type, int forNum, int execTime, int num) {
        String result = "";
        String oneHaoMa = "";
        int min = execTime;//开始计算的时候，就设置循环执行的次数
        for (int k = 0; k < forNum; k++) {
            if (type == 1) {
                oneHaoMa = shuangSeQiu().toString();
            } else {
                oneHaoMa = daLeTou().toString();
            }
            oneHaoMa = oneHaoMa.substring(1, oneHaoMa.length() - 1);
            int tempCount = nextCaipiao(oneHaoMa, type, execTime, num);
            if (tempCount < min) {
                min = tempCount;
                result = oneHaoMa;
                System.out.println("最快出现的一组：" + result + ",找到所需次数：" + min);
            }
        }

        return result;
    }

    /**
     * 给出当前的彩票号码，预测计算出下一次号码
     *
     * @param currentCaipiao
     * @param type           1:双色球  2：大乐透
     * @param execTime       如果一直未找到，则执行多少次后，返回一组号码   默认1百万
     * @param num            匹配几个号码即为找到，因为7个太难找到了
     * @return 返回匹配执行的次数
     */
    public static int nextCaipiao(String currentCaipiao, int type, int execTime, int num) {
        String ssq = null;
        String dlt = null;
        int count = 0;//进行多少次运算
        String[] strs = currentCaipiao.split(",");
        List<String> oldlist = Arrays.asList(strs);
        List<String> ssqList = null;
        List<String> dltList = null;
        while (true) {
            count++;
            int tempNum = 0;//用于计数，对应几个球
            if (type == 1) {
                ssqList = shuangSeQiu();
                int ssqLen = ssqList.size();
                int oldLen = oldlist.size();
                for (int i = 0; i < ssqLen - 1; i++) {
                    for (int j = 0; j < oldLen - 1; j++) {
                        if (ssqList.get(i).trim().equals(oldlist.get(j).trim())) {
                            tempNum += 1;
                        }
                    }
                }
                if (num == 7) {
                    if (ssqList.get(ssqLen - 1).trim().equals(oldlist.get(oldLen - 1).trim())) {
                        tempNum += 1;
                    }
                }

                if (tempNum >= num) {
                    System.out.println("匹配的一组双色球是：" + ssqList.toString());
                    break;
                }
            } else {
                dltList = daLeTou();
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (dltList.get(i).trim().equals(oldlist.get(j).trim())) {
                            tempNum += 1;
                        }
                    }
                }
                if (num >= 6 && tempNum == 5) {
                    for (int i = 5; i < 7; i++) {
                        for (int j = 5; j < 7; j++) {
                            if (dltList.get(i).trim().equals(oldlist.get(j).trim())) {
                                tempNum += 1;
                            }
                        }
                    }
                }

                if (tempNum >= num) {
                    System.out.println("匹配的一组大乐透是：" + dltList.toString());
                    break;
                }
            }
            //System.out.println("次数：" + count);
            if (count > execTime) {//1百万次还没有找到就随机返回一组彩票号码
                break;
            }
        }
        //handlerLotNum(dataList);
        /*if(type == 1){
            return shuangSeQiu().toString();
        }else{
            return daLeTou().toString();
        }*/
        return count;

    }

    /**
     * 给出当前的彩票号码，预测计算出下一次号码
     *
     * @param currentCaipiao
     * @param type           1:双色球  2：大乐透
     * @param execTime       如果一直未找到，则执行多少次后，返回一组号码   默认1百万
     * @return
     */
    public static String nextCaipiao(String currentCaipiao, int type, int execTime) {
        String ssq = null;
        String dlt = null;
        int count = 0;//进行多少次运算
        List<String> dataList = new ArrayList<>();//用于存放每次的出球
        List<String> ssqList = null;
        List<String> dltList = null;
        while (true) {
            count++;
            if (type == 1) {
                ssqList = shuangSeQiu();
                dataList.addAll(ssqList);
                ssq = ssqList.toString();
                if (currentCaipiao.equals(ssq)) {
                    break;
                }
            } else {
                dltList = daLeTou();
                dataList.addAll(dltList);
                dlt = dltList.toString();
                if (currentCaipiao.equals(dlt)) {
                    break;
                }
            }
//            System.out.println("次数：" + count);
            if (count > execTime) {//1百万次还没有找到就随机返回一组彩票号码
                break;
            }
        }
        //handlerLotNum(dataList);
        if (type == 1) {
            return shuangSeQiu().toString();
        } else {
            return daLeTou().toString();
        }
    }

    /**
     * 双色球
     *
     * @return
     */
    public static List<String> shuangSeQiu() {
        int direct = 6;//6点钟方向
        //红球
        int redNum = 6;//红色个数
        int redLen = 33;
        int blueNum = 1;//蓝色个数
        int blueLen = 16;
        return caiPiaoList(direct, redNum, redLen, blueNum, blueLen);
    }

    /**
     * 大乐透
     *
     * @return
     */
    public static List<String> daLeTou() {
        int direct = 12;
        int redNum = 5;//红色个数
        int redLen = 35;
        int blueNum = 2;//蓝色个数
        int blueLen = 12;
        return caiPiaoList(direct, redNum, redLen, blueNum, blueLen);
    }

    /**
     * 一组彩票数据 大乐透或双色球
     *
     * @param direct  几点钟方向出球
     * @param redNum  红色数量 出球
     * @param redLen  红色球总数量
     * @param blueNum 蓝色数量 出球
     * @param blueLen 蓝色球总数量
     * @return
     */
    public static List<String> caiPiaoList(int direct, int redNum, int redLen, int blueNum, int blueLen) {
        List<String> redList = cal(redLen, direct, redNum);
        Collections.sort(redList);
        List<String> blueList = cal(blueLen, direct, blueNum);
        Collections.sort(blueList);
        redList.addAll(blueList);
        return redList;
    }

    /**
     * 出球红球或篮球数组
     *
     * @param len    长度
     * @param direct 方向
     * @param num    出几个球
     * @return
     */
    private static List<String> cal(int len, int direct, int num) {
        List<String> oldBall = new ArrayList<>();//已出球
        for (int i = 1; i <= num; i++) {
            oldBall.add(outBall(len, direct, 200, oldBall));
        }
        return oldBall;
    }

    /**
     * 出球
     *
     * @param len     篮球或红球的数组长度
     * @param direct  几点钟方向出球 双色球 6点，大乐透12点
     * @param time    降慢执行速度，以便出球
     * @param oldBall 已出的球，要从集合中去掉
     * @return
     */
    private static String outBall(int len, int direct, int time, List<String> oldBall) {
        //循环无数次，直到某个数字和几点钟方向一致，则出球
        String[] redArr = getAutoArr(len);
        int oldLen = oldBall.size();
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            boolean flag = true;//该球还未出
            for (int j = 0; j < oldLen; j++) {
                if (redArr[i].equals(oldBall.get(j))) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                newList.add(redArr[i]);
            }
        }
        int count = 0;//当5s之后还没有出球，则降低执行速度 即count = 5000
        int newLen = newList.size();
        List<Integer> tempList = null;
        while (true) {
            tempList = getRandomNumList(newLen, 1, len + 1);
            boolean flag = false;
            int xb = 0;//记录出球的下标
            int tempLen = tempList.size();
            for (int i = 0; i < tempLen; i++) {
                if (direct == tempList.get(i)) {
                    xb = i;
                    flag = true;
                    break;
                }
            }
            if (flag) {
                return newList.get(xb);
            }
            if (count > 5000) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                count++;
            }
        }
    }

    //定义生成随机数并且装入集合容器的方法
    //方法的形参列表分别为：生成随机数的个数、生成随机数的值的范围最小值为start(包含start)、值得范围最大值为end(不包含end)  可取值范围可表示为[start,end)
    private static List<Integer> getRandomNumList(int nums, int start, int end) {
        //1.创建集合容器对象
        List<Integer> list = new ArrayList();

        //2.创建Random对象
        Random r = new Random();
        //循环将得到的随机数进行判断，如果随机数不存在于集合中，则将随机数放入集合中，如果存在，则将随机数丢弃不做操作，进行下一次循环，直到集合长度等于nums
        while (list.size() != nums) {
            int num = r.nextInt(end - start) + start;
            if (!list.contains(num)) {
                list.add(num);
            }
        }

        return list;
    }

    /**
     * 生成字符串数组
     *
     * @param len
     * @return
     */
    private static String[] getAutoArr(int len) {
        String[] strArr = new String[len];
        for (int i = 0; i < len; i++) {
            if (i < 9) {
                strArr[i] = "0" + (i + 1);
            } else {
                strArr[i] = "" + (i + 1);
            }
        }
        return strArr;
    }

    /**
     * 高频数
     *
     * @param allData
     * @param outNum  输出多少位高频数字
     * @return
     */
    private static List<String> handlerLotNum(List<String> allData, int outNum) {
        Map<String, Integer> mathMap = new LinkedHashMap<>();
        for (String temp : allData) {
            Integer count = mathMap.get(temp);
            mathMap.put(temp, (count == null) ? 1 : count + 1);
        }
        ArrayList<Map.Entry<String, Integer>> entries = sortMap(mathMap);
        int size = entries.size();
        int n = 0;
        List<String> lotNum = new ArrayList<>();//执行次数中出现最多的一组数据
        for (int i = 0; i < size; i++) {
            //System. out.println(entries.get(i).getKey()+":" +entries.get(i).getValue());
            if (n < outNum) {
                lotNum.add(entries.get(i).getKey());
                n++;
            }
        }
        return lotNum;
    }

    private static ArrayList<Map.Entry<String, Integer>> sortMap(Map map) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        return (ArrayList<Map.Entry<String, Integer>>) entries;
    }
}


