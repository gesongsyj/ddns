package com.xiang.ddns;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse.Record;
import com.xiang.ddns.pojo.Aliyun;
import com.xiang.ddns.util.AliDdnsUtils;
import com.xiang.ddns.util.LocalPublicIpv4;
import com.xiang.ddns.util.PropertiesUtil;

/**
 * 调用阿里api,更新DNS域名解析
 *
 * @author xiang
 */
public class UpdateDomainRecord {
    /**
     * 设置域名参数
     *
     * @param request
     */
    public void setParam(DescribeDomainRecordsRequest request) {
        String domainName = PropertiesUtil.getProperty("DomainName");
        request.putQueryParameter("DomainName", domainName);
    }

    /**
     * 解析DNS信息
     */
    public void analysisDns() {
        try {
            // 获取公网ip
            LocalPublicIpv4 ip = new LocalPublicIpv4();
            String ipV4 = ip.publicip();
            // 获取解析的数据
            String actionName = "DescribeDomainRecords";
            DescribeDomainRecordsResponse response;
            // 获取request
            DescribeDomainRecordsRequest request = AliDdnsUtils.getRequestQuery(actionName);
            // 设置request参数
            setParam(request);
            response = AliDdnsUtils.getClient().getAcsResponse(request);
            // 声明解析对象
            DemoListDomains demo = new DemoListDomains();
            // 获取阿里云的数据
            List<Record> list = response.getDomainRecords();
            if (list == null || list.isEmpty()) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                //更新ip
                Record record = list.get(i);
                Aliyun yun = new Aliyun();
                // 进行判定记录是否需要更新
                if (record.getValue().equals(ipV4)) {
                    // 不需要更新，继续下次循环
                    System.out.println("当前域名解析地址为：" + ipV4 + "不需要更新！");
                } else {
                    System.out.println("更新域名：" + record.getDomainName() + "至IP：" + ipV4);
                    // 进行替换关键数据
                    yun.setIpV4(ipV4);
                    yun.setRecordId(record.getRecordId());
                    yun.setRr(record.getRR());
                    yun.setTTL(record.getTTL());
                    yun.setType(record.getType());
                    System.out.println("域名更换ip开始");
                    demo.analysisDns(yun);
                    System.out.println("域名更换ip结束");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("域名更换异常");
        }
    }

    /**
     * 解析DNS信息
     * 使用vpn时的内网ip
     */
    public void myAnalysisDns() {
        // 找到启用vpn后的内网ip
        List<String> localIPList = getLocalIPList();
        String innerIp = filterOne(localIPList, "10");
        // 获取公网ip
        LocalPublicIpv4 ip = new LocalPublicIpv4();
        String ipV4 = ip.publicip();
        // 获取解析的数据
        String actionName = "DescribeDomainRecords";
        DescribeDomainRecordsResponse response;
        // 获取request
        DescribeDomainRecordsRequest request = AliDdnsUtils.getRequestQuery(actionName);
        // 设置request参数
        setParam(request);
        try {
            response = AliDdnsUtils.getClient().getAcsResponse(request);
            // 声明解析对象
            DemoListDomains demo = new DemoListDomains();
            // 获取阿里云的数据
            List<Record> list = response.getDomainRecords();
            if (list == null || list.isEmpty()) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                //更新ip
                Record record = list.get(i);
                Aliyun yun = new Aliyun();
                if("TXT".equals(record.getType())){
                    if(innerIp!=null){
                        // 进行判定记录是否需要更新
                        if (record.getValue().equals(innerIp)) {
                            // 不需要更新，继续下次循环
                            System.out.println("当前域名内网解析地址为：" + innerIp + "不需要更新！");
                        } else {
                            System.out.println("更新域名：" + record.getDomainName() + "至IP：" + innerIp);
                            // 进行替换关键数据
                            yun.setIpV4(innerIp);
                            yun.setRecordId(record.getRecordId());
                            yun.setRr(record.getRR());
                            yun.setTTL(record.getTTL());
                            yun.setType(record.getType());
                            System.out.println("域名更换内网ip开始");
                            demo.analysisDns(yun);
                            System.out.println("域名更换内网ip结束");
                        }
                    }
                }else{
                    // 进行判定记录是否需要更新
                    if (record.getValue().equals(ipV4)) {
                        // 不需要更新，继续下次循环
                        System.out.println("当前域名解析地址为：" + ipV4 + "不需要更新！");
                    } else {
                        System.out.println("更新域名：" + record.getDomainName() + "至IP：" + ipV4);
                        // 进行替换关键数据
                        yun.setIpV4(ipV4);
                        yun.setRecordId(record.getRecordId());
                        yun.setRr(record.getRR());
                        yun.setTTL(record.getTTL());
                        yun.setType(record.getType());
                        System.out.println("域名更换ip开始");
                        demo.analysisDns(yun);
                        System.out.println("域名更换ip结束");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("域名更换异常");
        }
    }

    //获取本机所有IP地址
    private List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }

    private String filterOne(List<String> ipList,String preFix){
        for (String ip : ipList) {
            if (ip.startsWith(preFix)){
                return ip;
            }
        }
        return null;
    }
}
