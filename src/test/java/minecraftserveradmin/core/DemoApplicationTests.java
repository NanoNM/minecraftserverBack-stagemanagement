package minecraftserveradmin.core;

import minecraftserveradmin.core.services.impl.UserUserImpl;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@SpringBootTest
class DemoApplicationTests {


//    private static void printComputerSystem(final ComputerSystem computerSystem) {
//        System.out.println("manufacturer: " + computerSystem.getManufacturer());
//        System.out.println("model: " + computerSystem.getModel());
//        System.out.println("serialnumber: " + computerSystem.getSerialNumber());
//        final Firmware firmware = computerSystem.getFirmware();
//        System.out.println("firmware:");
//        System.out.println("  manufacturer: " + firmware.getManufacturer());
//        System.out.println("  name: " + firmware.getName());
//        System.out.println("  description: " + firmware.getDescription());
//        System.out.println("  version: " + firmware.getVersion());
//        System.out.println("  release date: " + (firmware.getReleaseDate() == null ? "unknown"
//                : firmware.getReleaseDate() == null ? "unknown" : FormatUtil.formatDate(firmware.getReleaseDate())));
//        final Baseboard baseboard = computerSystem.getBaseboard();
//        System.out.println("baseboard:");
//        System.out.println("  manufacturer: " + baseboard.getManufacturer());
//        System.out.println("  model: " + baseboard.getModel());
//        System.out.println("  version: " + baseboard.getVersion());
//        System.out.println("  serialnumber: " + baseboard.getSerialNumber());
//    }
//    private static void printProcessor(CentralProcessor processor) {
//        System.out.println(processor.getName());
//        System.out.println(" " + processor.getPhysicalProcessorCount() + " physical CPU(s)");
//        System.out.println(" " + processor.getLogicalProcessorCount() + " logical CPU(s)");
//        System.out.println("Identifier: " + processor.getIdentifier());
//        System.out.println("ProcessorID: " + processor.getProcessorID());
//    }
//    private static void printMemory(GlobalMemory memory) {
//        System.out.println("以使用内存: " + FormatUtil.formatBytes(memory.getAvailable()) + "总共内存"
//                + FormatUtil.formatBytes(memory.getTotal()));
//        System.out.println("Swap used: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/"
//                + FormatUtil.formatBytes(memory.getSwapTotal()));
//    }
//    private static void printCpu(CentralProcessor processor) {
//        System.out.println("Uptime: " + FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
//        long[] prevTicks = processor.getSystemCpuLoadTicks();
//        System.out.println("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
//        // Wait a second...
//        Util.sleep(1000);
//        long[] ticks = processor.getSystemCpuLoadTicks();
//        System.out.println("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
//        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
//        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
//        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
//        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
//        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
//        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
//        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
//        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
//        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;
//        System.out.format(
//                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n",
//                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
//                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu);
//        System.out.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
//        System.out.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
//        double[] loadAverage = processor.getSystemLoadAverage(3);
//        System.out.println("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
//                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
//                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
//        // per core CPU
//        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
//        double[] load = processor.getProcessorCpuLoadBetweenTicks();
//        for (double avg : load) {
//            procCpu.append(String.format(" %.1f%%", avg * 100));
//        }
//        System.out.println(procCpu.toString());
//    }
//    private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
//        System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
//        // Sort by highest CPU
//        List<OSProcess> procs = Arrays.asList(os.getProcesses(5, OperatingSystem.ProcessSort.CPU));
//        System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
//        for (int i = 0; i < procs.size() && i < 5; i++) {
//            OSProcess p = procs.get(i);
//            System.out.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
//                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
//                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
//                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
//        }
//    }
//    private static void printSensors(Sensors sensors) {
//        System.out.println("Sensors:");
//        System.out.format(" CPU Temperature: %.1f°C%n", sensors.getCpuTemperature());
//        System.out.println(" Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
//        System.out.format(" CPU Voltage: %.1fV%n", sensors.getCpuVoltage());
//    }
//    private static void printPowerSources(PowerSource[] powerSources) {
//        StringBuilder sb = new StringBuilder("Power: ");
//        if (powerSources.length == 0) {
//            sb.append("Unknown");
//        } else {
//            double timeRemaining = powerSources[0].getTimeRemaining();
//            if (timeRemaining < -1d) {
//                sb.append("Charging");
//            } else if (timeRemaining < 0d) {
//                sb.append("Calculating time remaining");
//            } else {
//                sb.append(String.format("%d:%02d remaining", (int) (timeRemaining / 3600),
//                        (int) (timeRemaining / 60) % 60));
//            }
//        }
//        for (PowerSource pSource : powerSources) {
//            sb.append(String.format("%n %s @ %.1f%%", pSource.getName(), pSource.getRemainingCapacity() * 100d));
//        }
//        System.out.println(sb.toString());
//    }
//    private static void printDisks(HWDiskStore[] diskStores) {
//        System.out.println("Disks:");
//        for (HWDiskStore disk : diskStores) {
//            boolean readwrite = disk.getReads() > 0 || disk.getWrites() > 0;
//            System.out.format(" %s: (model: %s - S/N: %s) size: %s, reads: %s (%s), writes: %s (%s), xfer: %s ms%n",
//                    disk.getName(), disk.getModel(), disk.getSerial(),
//                    disk.getSize() > 0 ? FormatUtil.formatBytesDecimal(disk.getSize()) : "?",
//                    readwrite ? disk.getReads() : "?", readwrite ? FormatUtil.formatBytes(disk.getReadBytes()) : "?",
//                    readwrite ? disk.getWrites() : "?", readwrite ? FormatUtil.formatBytes(disk.getWriteBytes()) : "?",
//                    readwrite ? disk.getTransferTime() : "?");
//            HWPartition[] partitions = disk.getPartitions();
//            if (partitions == null) {
//                // TODO Remove when all OS's implemented
//                continue;
//            }
//            for (HWPartition part : partitions) {
//                System.out.format(" |-- %s: %s (%s) Maj:Min=%d:%d, size: %s%s%n", part.getIdentification(),
//                        part.getName(), part.getType(), part.getMajor(), part.getMinor(),
//                        FormatUtil.formatBytesDecimal(part.getSize()),
//                        part.getMountPoint().isEmpty() ? "" : " @ " + part.getMountPoint());
//            }
//        }
//    }
//    private static void printFileSystem(FileSystem fileSystem) {
//        System.out.println("File System:");
//        System.out.format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
//                fileSystem.getMaxFileDescriptors());
//        OSFileStore[] fsArray = fileSystem.getFileStores();
//        for (OSFileStore fs : fsArray) {
//            long usable = fs.getUsableSpace();
//            long total = fs.getTotalSpace();
//            System.out.format(
//                    " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
//                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
//                            + " and is mounted at %s%n",
//                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
//                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
//                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());
//        }
//    }
//    private static void printNetworkInterfaces(NetworkIF[] networkIFs) {
//        System.out.println("Network interfaces:");
//        for (NetworkIF net : networkIFs) {
//            System.out.format(" Name: %s (%s)%n", net.getName(), net.getDisplayName());
//            System.out.format("   MAC Address: %s %n", net.getMacaddr());
//            System.out.format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
//            System.out.format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr()));
//            System.out.format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr()));
//            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
//                    || net.getPacketsSent() > 0;
//            System.out.format("   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
//                    hasData ? net.getPacketsRecv() + " packets" : "?",
//                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
//                    hasData ? " (" + net.getInErrors() + " err)" : "",
//                    hasData ? net.getPacketsSent() + " packets" : "?",
//                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
//                    hasData ? " (" + net.getOutErrors() + " err)" : "");
//        }
//    }
//    private static void printNetworkParameters(NetworkParams networkParams) {
//        System.out.println("Network parameters:");
//        System.out.format(" Host name: %s%n", networkParams.getHostName());
//        System.out.format(" Domain name: %s%n", networkParams.getDomainName());
//        System.out.format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers()));
//        System.out.format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway());
//        System.out.format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway());
//    }
//    private static void printDisplays(Display[] displays) {
//        System.out.println("Displays:");
//        int i = 0;
//        for (Display display : displays) {
//            System.out.println(" Display " + i + ":");
//            System.out.println(display.toString());
//            i++;
//        }
//    }
//    private static void printUsbDevices(UsbDevice[] usbDevices) {
//        System.out.println("USB Devices:");
//        for (UsbDevice usbDevice : usbDevices) {
//            System.out.println(usbDevice.toString());
//        }
//    }


    @Autowired
    UserUserImpl userUser;
    @Test
    void contextLoads() {
        userUser.selectAllUser(1);

//        String f = "===============================================";
//        SystemInfo si = new SystemInfo();
//        HardwareAbstractionLayer hal = si.getHardware();
//        OperatingSystem os = si.getOperatingSystem();
//        System.out.println(os.getFamily());
////        printComputerSystem(hal.getComputerSystem());
//        System.out.println(f);
//        printProcessor(hal.getProcessor());
//        System.out.println(f);
//        printMemory(hal.getMemory());System.out.println(f);
//        printCpu(hal.getProcessor());System.out.println(f);
//        printProcesses(os, hal.getMemory());System.out.println(f);
//        printSensors(hal.getSensors());System.out.println(f);
//        printPowerSources(hal.getPowerSources());System.out.println(f);
////        printDisks(hal.getDiskStores());System.out.println(f);
//        printFileSystem(os.getFileSystem());System.out.println(f);
//        printNetworkInterfaces(hal.getNetworkIFs());System.out.println(f);
//        printNetworkParameters(os.getNetworkParams());System.out.println(f);
////        // hardware: displays
////        printDisplays(hal.getDisplays());System.out.println(f);
////        // hardware: USB devices
////        printUsbDevices(hal.getUsbDevices(true));System.out.println(f);
    }

}
