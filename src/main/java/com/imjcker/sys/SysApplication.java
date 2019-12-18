package com.imjcker.sys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@SpringBootApplication
public class SysApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SysApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("启动参数===========================start");
        for (String arg : args) {
            log.info(arg);
        }
        log.info("启动参数===========================end");
    }


    @GetMapping("/getTime")
    public Map<String, String> getTime() {
        return SysUtil.getTime();
    }

    @GetMapping("/getMemory")
    public Map<String, Long> getMemory() {
        return SysUtil.getMemory();
    }

    @GetMapping("/getVirtualMemory")
    public Map<String, Long> getVirtualMemory() {
        return SysUtil.getVirtualMemory();
    }
    @GetMapping("/getAllMemory")
    public Map<String, Long> getAllMemory() {
        Map<String, Long> memory = this.getMemory();
        memory.putAll(SysUtil.getVirtualMemory());
        return memory;
    }


    @GetMapping("/getDisk")
    public Map<String, Long> getDisk() {
        return SysUtil.getDisk();
    }

    @GetMapping("/getServerInfo")
    public String getServerInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        return os.toString();
    }

    @GetMapping("/getCpuInfo")
    public int getCpuInfo() {
        return SysUtil.printCpu();
    }
}