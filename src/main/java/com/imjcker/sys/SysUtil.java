package com.imjcker.sys;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.Constants;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.time.Instant;
import java.util.*;

@Slf4j
public class SysUtil {
    static List<String> oshi = new ArrayList<>();

    public static Map<String, String> getTime() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        Map<String, String> map = new HashMap<>();
        log.info("osName: {}", determineOsName(os.getManufacturer()));
        log.info("java os: {}", ManagementFactory.getOperatingSystemMXBean().getName());
        map.put("osName", determineOsName(os.getManufacturer()));
//        map.put("osName", determineOsName());
        map.put("bootTime", Instant.ofEpochSecond(os.getSystemBootTime()).toString());
        map.put("uptime", FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        return map;
    }

    private static String determineOsName(String manufacturer) {
        if (manufacturer.contentEquals("Microsoft") || manufacturer.contentEquals("windows")) {
            return "windows";
        }
        if (manufacturer.contentEquals("Apple") || manufacturer.contentEquals("Mac OS X")) {
            return "apple";
        }
        if (manufacturer.contentEquals("centos")) {
            return "centos";
        }
        if (manufacturer.contentEquals("ubuntu")) {
            return "ubuntu";
        }
        return determineOsName(ManagementFactory.getOperatingSystemMXBean().getName());
    }

    public static Map<String, Long> getMemory() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        Map<String, Long> map = new HashMap<>();
        GlobalMemory memory = hardware.getMemory();
        map.put("total", memory.getTotal() >> 20);
        map.put("available", memory.getAvailable() >> 20);
        return map;
    }

    public static Map<String, Long> getVirtualMemory() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        Map<String, Long> map = new HashMap<>();

        VirtualMemory virtualMemory = hardware.getMemory().getVirtualMemory();
        map.put("v_total", virtualMemory.getSwapTotal() >> 20);
        map.put("v_available", (virtualMemory.getSwapTotal() - virtualMemory.getSwapUsed()) >> 20);
        return map;
    }

    public static Map<String, Long> getDisk() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        Map<String, Long> map = new HashMap<>();

        FileSystem fileSystem = operatingSystem.getFileSystem();
        for (OSFileStore fs : fileSystem.getFileStores()) {
            long total = fs.getTotalSpace() >> 30;
            long usable = fs.getUsableSpace() >> 30;
            map.put("total", total);
            map.put("usable", usable);
        }
        return map;
    }

        public static void main(String[] args) {
//    public static SystemVO getSystemInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

            List<String> strings = printServices(os);
            strings.forEach(System.out::println);

            System.out.println("My PID: " + os.getProcessId() + " with affinity "
                    + Long.toBinaryString(os.getProcessAffinityMask(os.getProcessId())));
        }


    private static void printComputerSystem(final ComputerSystem computerSystem) {
        oshi.add("系统架构: " + computerSystem.toString());
        oshi.add(" firmware: " + computerSystem.getFirmware().toString());
        oshi.add(" baseboard: " + computerSystem.getBaseboard().toString());
    }

    private static void printProcessor(CentralProcessor processor) {
        log.info("CPU信息：");
        oshi.add(processor.toString());
    }


    public static int printCpu() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        return new Double(processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100).intValue();
    }

    private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
        oshi.add("My PID: " + os.getProcessId() + " with affinity "
                + Long.toBinaryString(os.getProcessAffinityMask(os.getProcessId())));
        oshi.add("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = Arrays.asList(os.getProcesses(5, OperatingSystem.ProcessSort.CPU));

        oshi.add("   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            oshi.add(String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
        }
    }

    private static List<String> printServices(OperatingSystem os) {
        List<String> services = new ArrayList<>();
        services.add("Services: ");
        services.add("   PID   State   Name");
        // DO 5 each of running and stopped
        int i = 0;
        for (OSService s : os.getServices()) {
            if (s.getState().equals(OSService.State.RUNNING) && i++ < 5) {
                services.add(String.format(" %5d  %7s  %s", s.getProcessID(), s.getState(), s.getName()));
            }
        }
        i = 0;
        for (OSService s : os.getServices()) {
            if (s.getState().equals(OSService.State.STOPPED) && i++ < 5) {
                services.add(String.format(" %5d  %7s  %s", s.getProcessID(), s.getState(), s.getName()));
            }
        }
        return services;
    }

    private static void printSensors(Sensors sensors) {
        oshi.add("Sensors: " + sensors.toString());
    }

    private static void printPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder("Power Sources: ");
        if (powerSources.length == 0) {
            sb.append("Unknown");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("\n ").append(powerSource.toString());
        }
        oshi.add(sb.toString());
    }

    private static void printDisplays(Display[] displays) {
        oshi.add("Displays:");
        int i = 0;
        for (Display display : displays) {
            oshi.add(" Display " + i + ":");
            oshi.add(String.valueOf(display));
            i++;
        }
    }

    private static void printUsbDevices(UsbDevice[] usbDevices) {
        oshi.add("USB Devices:");
        for (UsbDevice usbDevice : usbDevices) {
            oshi.add(String.valueOf(usbDevice));
        }
    }

    private static void printSoundCards(SoundCard[] cards) {
        oshi.add("Sound Cards:");
        for (SoundCard card : cards) {
            oshi.add(" " + String.valueOf(card));
        }
    }


    @Builder
    public static class SystemVO {
        private String os;
        private String bootTime;
        private String uptime;

        private float globalMemoryTotal;
        private float globalMemoryAvailable;
        private float virtualMemoryTotal;
        private float virtualMemoryAvailable;

        private List<String> disks = new ArrayList<>();
        private List<String> fileStore = new ArrayList<>();

        private String hostName;
        private String iPv4addr;

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

}
