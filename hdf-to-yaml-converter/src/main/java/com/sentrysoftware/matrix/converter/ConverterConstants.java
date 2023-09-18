package com.sentrysoftware.matrix.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterConstants {

	public static final String EMPTY_STRING = "";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String COLON = ":";
	public static final String SPACE = " ";
	public static final String COLON_SPACE = COLON + SPACE;
	public static final String DOT = ".";
	public static final String OPENING_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";

	public static final String TRUE = "true";
	public static final String ONE = "1";
	public static final String PERCENT = "%";

	public static final String DOUBLE_QUOTES_REGEX_REPLACEMENT = "^\\s*\"(.*)\"\\s*$";
	public static final String SOURCE_REFERENCE_REGEX_REPLACEMENT = "^\\s*%(.*)%\\s*$";

	public static final String INTEGER_REGEX = "^[1-9]\\d*$";
	public static final String EMBEDDED_FILE_REGEX = "^\\s*embeddedfile\\(([1-9]\\d*)\\)\\s*$";

	public static final String CONNECTOR = "connector";

	public static final String DETECTION = "detection";
	public static final String CRITERIA = "criteria";

	public static final String DISCOVERY = "discovery";
	public static final String COLLECT = "collect";

	public static final String MONO_INSTANCE_CAMEL_CASE = "monoInstance";
	public static final String MULTI_INSTANCE_CAMEL_CASE = "multiInstance";

	public static final String MONO_INSTANCE = MONO_INSTANCE_CAMEL_CASE.toLowerCase();
	public static final String MULTI_INSTANCE = MULTI_INSTANCE_CAMEL_CASE.toLowerCase();

	public static final String TYPE = "type";
	public static final String VALUE_TABLE = "valuetable";

	public static final String DETECTION_DOT_CRITERIA = "detection.criteria";
	public static final String DOT_COMPUTE = ".compute";

	public static final String SET_COLUMN = "setColumn";
	public static final String SET_EXPECTED_RESULT = "setExpectedResult";
	public static final String SET_ERROR_MESSAGE = "setErrorMessage";
	public static final String SET_WBEM_QUERY = "setWbemQuery";
	public static final String SET_WBEM_NAMESPACE = "setWbemNamespace";
	public static final String SET_TIMEOUT = "setTimeout";
	public static final String SET_TEXT = "setText";

	public static final String DEFAULT = "default";

	public static final String IPMI_TOOL = "ipmitool";

	public static final String SOURCES = "sources";
	public static final String MONITORS = "monitors";
	public static final String COMPUTES = "computes";
	public static final String MAPPING = "mapping";
	public static final String ATTRIBUTES = "attributes";
	public static final String SOURCE = "source";
	public static final String METRICS = "metrics";
	public static final String LEGACY_TEXT_PARAMETERS = "legacyTextParameters";
	public static final String CONNECTION_TYPES = "connectionTypes";
	public static final String CONDITIONAL_COLLECTION = "conditionalCollection";

	// Monitor types
	public static final String HDF_BATTERY = "battery";
	public static final String HDF_BLADE = "blade";
	public static final String HDF_CPU = "cpu";
	public static final String HDF_CPU_CORE = "cpucore";
	public static final String HDF_DISK_CONTROLLER = "diskcontroller";
	public static final String HDF_ENCLOSURE = "enclosure";
	public static final String HDF_FAN = "fan";
	public static final String HDF_GPU = "gpu";
	public static final String HDF_LED = "led";
	public static final String HDF_LOGICAL_DISK = "logicaldisk";
	public static final String HDF_LUN = "lun";
	public static final String HDF_MEMORY = "memory";
	public static final String HDF_NETWORK_CARD = "networkcard";
	public static final String HDF_OTHER_DEVICE = "otherdevice";
	public static final String HDF_PHYSICAL_DISK = "physicaldisk";
	public static final String HDF_POWER_SUPPLY = "powersupply";
	public static final String HDF_ROBOTIC = "robotic";
	public static final String HDF_TAPEDRIVE = "tapedrive";
	public static final String HDF_TEMPERATURE = "temperature";
	public static final String HDF_VM = "vm";
	public static final String HDF_VOLTAGE = "voltage";

	public static final String YAML_BATTERY = HDF_BATTERY;
	public static final String YAML_BLADE = HDF_BLADE;
	public static final String YAML_CPU = HDF_CPU;
	public static final String YAML_CPU_CORE = "cpu_core";
	public static final String YAML_DISK_CONTROLLER = "disk_controller";
	public static final String YAML_ENCLOSURE = HDF_ENCLOSURE;
	public static final String YAML_FAN = HDF_FAN;
	public static final String YAML_GPU = HDF_GPU;
	public static final String YAML_LED = HDF_LED;
	public static final String YAML_LOGICAL_DISK = "logical_disk";
	public static final String YAML_LUN = HDF_LUN;
	public static final String YAML_MEMORY = HDF_MEMORY;
	public static final String YAML_NETWORK = "network";
	public static final String YAML_OTHER_DEVICE = "other_device";
	public static final String YAML_PHYSICAL_DISK = "physical_disk";
	public static final String YAML_POWER_SUPPLY = "power_supply";
	public static final String YAML_ROBOTICS = "robotics";
	public static final String YAML_TAPEDRIVE = "tape_drive";
	public static final String YAML_TEMPERATURE = HDF_TEMPERATURE;
	public static final String YAML_VM = HDF_VM;
	public static final String YAML_VOLTAGE = HDF_VOLTAGE;

	// HDF properties
	public static final String HDF_CHEMISTRY = "chemistry";
	public static final String HDF_TYPE = "type";
	public static final String HDF_MODEL = "model";
	public static final String HDF_VENDOR = "vendor";
	public static final String HDF_DISPLAY_ID = "displayid";
	public static final String HDF_DEVICE_ID = "deviceid";
	public static final String HDF_BLADE_MODEL = "blademodel";
	public static final String HDF_BLADE_NAME = "bladename";
	public static final String HDF_SERIAL_NUMBER = "serialnumber";
	public static final String HDF_MAX_POWER_CONSUMPTION = "maxpowerconsumption";
	public static final String HDF_MAXIMUM_SPEED = "maximumspeed";
	public static final String HDF_CORRECTED_ERROR_WARNING_THRESHOLD = "correctederrorwarningthreshold";
	public static final String HDF_CORRECTED_ERROR_ALARM_THRESHOLD = "correctederroralarmthreshold";
	public static final String HDF_CONTROLLER_NUMBER = "controllernumber";
	public static final String HDF_FIRMWARE_VERSION = "firmwareversion";
	public static final String HDF_DRIVER_VERSION = "driverversion";
	public static final String HDF_COLOR = "color";
	public static final String HDF_BLINKING_STATUS = "blinkingstatus";
	public static final String HDF_OFF_STATUS = "offstatus";
	public static final String HDF_ON_STATUS = "onstatus";
	public static final String HDF_LED_NAME = "name";
	public static final String HDF_LOGICALDISK_TYPE = "logicaldisktype";
	public static final String HDF_RAID_LEVEL = "raidlevel";
	public static final String HDF_SIZE = "size";
	public static final String HDF_DEVICE_HOSTNAME = "devicehostname";
	public static final String HDF_BIOS_VERSION = "biosversion";
	public static final String HDF_ERROR_COUNT_WARNING_THRESHOLD = "errorcountwarningthreshold";
	public static final String HDF_ERROR_COUNT_ALARM_THRESHOLD = "errorcountalarmthreshold";
	public static final String HDF_ADDITIONAL_LABEL = "additionallabel";
	public static final String HDF_VALUE_WARNING_THRESHOLD = "valuewarningthreshold";
	public static final String HDF_VALUE_ALARM_THRESHOLD = "valuealarmthreshold";
	public static final String HDF_USAGE_COUNT_WARNING_THRESHOLD = "usagecountwarningthreshold";
	public static final String HDF_USAGE_COUNT_ALARM_THRESHOLD = "usagecountalarmthreshold";
	public static final String HDF_DEVICE_TYPE = "devicetype";
	public static final String HDF_WARNING_THRESHOLD = "warningthreshold";
	public static final String HDF_ALARM_THRESHOLD = "alarmthreshold";
	public static final String HDF_PERCENT_WARNING_THRESHOLD = "percentwarningthreshold";
	public static final String HDF_PERCENT_ALARM_THRESHOLD = "percentalarmthreshold";
	public static final String HDF_WWN = "wwn";
	public static final String HDF_LOCAL_DEVICE_NAME = "localdevicename";
	public static final String HDF_REMOTE_DEVICE_NAME = "remotedevicename";
	public static final String HDF_ARRAY_NAME = "arrayname";
	public static final String HDF_AVAILABLE_PATH_WARNING = "availablepathwarning";
	public static final String HDF_AVAILABLE_PATH_INFORMATION = "availablepathinformation";
	public static final String HDF_AVAILABLE_PATH_COUNT = "availablepathcount";
	public static final String HDF_POWER_SUPPLY_TYPE = "powersupplytype";
	public static final String HDF_POWER_SUPPLY_POWER = "powersupplypower";
	public static final String HDF_ROBOTIC_TYPE = "robotictype";
	public static final String HDF_LOWER_THRESHOLD = "lowerthreshold";
	public static final String HDF_UPPER_THRESHOLD = "upperthreshold";
	public static final String HDF_VOLTAGE_TYPE = "voltagetype";
	public static final String HDF_TEMPERATURE_TYPE = "temperaturetype";
	public static final String HDF_FAN_TYPE = "fantype";
	public static final String HDF_HOSTNAME = "hostname";
	public static final String HDF_USE_FOR_CAPACITY_REPORT = "useforcapacityreport";

	// YAML attributes
	public static final String YAML_TYPE = HDF_TYPE;
	public static final String YAML_MODEL = HDF_MODEL;
	public static final String YAML_VENDOR = HDF_VENDOR;
	public static final String YAML_DISPLAY_ID = "__display_id";
	public static final String YAML_ID = "id";
	public static final String YAML_NAME = "name";
	public static final String YAML_HW_PARENT_ID = "hw.parent.id";
	public static final String YAML_HW_PARENT_TYPE = "hw.parent.type";
	public static final String YAML_CHEMISTRY = HDF_CHEMISTRY;
	public static final String YAML_BLADE_NAME = "blade_name";
	public static final String YAML_SERIAL_NUMBER = "serial_number";
	public static final String YAML_CONTROLLER_NUMBER = "controller_number";
	public static final String YAML_BIOS_VERSION = "bios_version";
	public static final String YAML_FIRMWARE_VERSION = "firmware_version";
	public static final String YAML_DRIVER_VERSION = "driver_version";
	public static final String YAML_BLINKING_STATUS = "__blinking_status";
	public static final String YAML_ON_STATUS = "__on_status";
	public static final String YAML_OFF_STATUS = "__off_status";
	public static final String YAML_LED_NAME = "__name";
	public static final String YAML_LED_COLOR = HDF_COLOR;
	public static final String YAML_RAID_LEVEL = "raid_level";
	public static final String YAML_DEVICE_HOSTNAME = "device_hostname";
	public static final String YAML_ERROR_COUNT_WARNING_THRESHOLD = "errorcountwarningthreshold";
	public static final String YAML_ERROR_COUNT_ALARM_THRESHOLD = "errorcountalarmthreshold";
	public static final String YAML_SIZE = "size";
	public static final String YAML_MEMORY_LIMIT = "hw.memory.limit";
	public static final String YAML_ADDITIONAL_LABEL = "additional_label";
	public static final String YAML_OTHER_DEVICE_VALUE_WARNING_THRESHOLD =
		"hw.other_device.value.limit{limit_type=\"degraded\"}";
	public static final String YAML_OTHER_DEVICE_VALUE_ALARM_THRESHOLD =
		"hw.other_device.value.limit{limit_type=\"critical\"}";
	public static final String YAML_OTHER_DEVICE_USAGE_COUNT_WARNING_THRESHOLD =
		"hw.other_device.uses.limit{limit_type=\"degraded\"}";
	public static final String YAML_OTHER_DEVICE_USAGE_COUNT_ALARM_THRESHOLD =
		"hw.other_device.uses.limit{limit_type=\"critical\"}";
	public static final String YAML_DEVICE_TYPE = "device_type";
	public static final String YAML_REMOTE_DEVICE_NAME = "remote_device_name";
	public static final String YAML_LOCAL_DEVICE_NAME = "local_device_name";
	public static final String YAML_ARRAY_NAME = "array_name";
	public static final String YAML_POWER_SUPPLY_TYPE = "power_supply_type";
	public static final String YAML_ROBOTICS_TYPE = "robotics_type";
	public static final String YAML_VOLTAGE_LOW_CRITICAL = "hw.voltage.limit{limit_type=\"low.critical\"}";
	public static final String YAML_VOLTAGE_HIGH_DEGRADED = "hw.voltage.limit{limit_type=\"high.degraded\"}";
	public static final String YAML_SENSOR_LOCATION = "sensor_location";
	public static final String YAML_TEMPERATURE_TYPE = "temperature_type";
	public static final String YAML_TEMPERATURE_LIMIT_DEGRADED = "hw.temperature.limit{limit_type=\"high.degraded\"}";
	public static final String YAML_TEMPERATURE_LIMIT_CRITICAL = "hw.temperature.limit{limit_type=\"high.critical\"}";
	public static final String YAML_PHYSICAL_ADDRESS = "physical_address";
	public static final String YAML_PHYSICAL_ADDRESS_TYPE = "physical_address_type";
	public static final String YAML_LOGICAL_ADDRESS = "logical_address";
	public static final String YAML_LOGICAL_ADDRESS_TYPE = "logical_address_type";
	public static final String YAML_VM_HOSTNAME = "vm.host.name";
	public static final String YAML_USE_FOR_CAPACITY_REPORT = "__use_for_capacity_report";
	public static final String YAML_BANDWIDTH = "bandwidth";

	// HDF Collect parameters
	public static final String HDF_STATUS = "status";
	public static final String HDF_TIME_LEFT = "timeleft";
	public static final String HDF_STATUS_INFORMATION = "statusinformation";
	public static final String HDF_CHARGE = "charge";
	public static final String HDF_POWER_STATE = "powerstate";
	public static final String HDF_PREDICTED_FAILURE = "predictedfailure";
	public static final String HDF_CURRENT_SPEED = "currentspeed";
	public static final String HDF_ERROR_COUNT = "errorcount";
	public static final String HDF_CORRECTED_ERROR_COUNT = "correctederrorcount";
	public static final String HDF_POWER_CONSUMPTION = "powerconsumption";
	public static final String HDF_USAGE_COUNT = "usagecount";
	public static final String HDF_VALUE = "value";
	public static final String HDF_CONTROLLER_STATUS = "controllerstatus";
	public static final String HDF_SPEED = "speed";
	public static final String HDF_SPEED_PERCENT = "speedpercent";
	public static final String HDF_LED_INDICATOR = "ledindicator";
	public static final String HDF_UNALLOCATED_SPACE = "unallocatedspace";
	public static final String HDF_INTRUSION_STATUS = "intrusionstatus";
	public static final String HDF_ENERGY_USAGE = "energyusage";
	public static final String HDF_USED_TIME_PERCENT = "usedtimepercent";
	public static final String HDF_DECODER_USED_TIME_PERCENT = "decoderusedtimepercent";
	public static final String HDF_ENCODER_USED_TIME_PERCENT = "encoderusedtimepercent";
	public static final String HDF_MEMORY_UTILIZATION = "memoryutilization";
	public static final String HDF_RECEIVED_BYTES = "receivedbytes";
	public static final String HDF_TRANSMITTED_BYTES = "transmittedbytes";
	public static final String HDF_RECEIVED_BYTES_RATE = "receivedbytesrate";
	public static final String HDF_TRANSMITTED_BYTES_RATE = "transmittedbytesrate";
	public static final String HDF_USED_TIME_PERCENT_WARNING_THRESHOLD = "usedtimepercentwarningthreshold";
	public static final String HDF_USED_TIME_PERCENT_ALARM_THRESHOLD = "usedtimepercentalarmthreshold";
	public static final String HDF_MEMORY_UTILIZATION_WARNING_THRESHOLD = "memoryutilizationwarningthreshold";
	public static final String HDF_MEMORY_UTILIZATION_ALARM_THRESHOLD = "memoryutilizationalarmthreshold";
	public static final String HDF_TRANSPORT_ERROR_COUNT = "transporterrorcount";
	public static final String HDF_ILLEGAL_REQUEST_ERROR_COUNT = "illegalrequesterrorcount";
	public static final String HDF_NO_DEVICE_ERROR_COUNT = "nodeviceerrorcount";
	public static final String HDF_DEVICE_NOT_READY_ERROR_COUNT = "devicenotreadyerrorcount";
	public static final String HDF_RECOVERABLE_ERROR_COUNT = "recoverableerrorcount";
	public static final String HDF_HARD_ERROR_COUNT = "harderrorcount";
	public static final String HDF_MEDIA_ERROR_COUNT = "mediaerrorcount";
	public static final String HDF_ENDURANCE_REMAINING = "enduranceremaining";
	public static final String HDF_VM_POWER_STATE = "powerstate";
	public static final String HDF_VM_POWER_RATIO = "powershare";
	public static final String HDF_USED_PERCENT = "usedpercent";
	public static final String HDF_USED_WATTS = "usedwatts";
	public static final String HDF_MOVE_COUNT = "movecount";
	public static final String HDF_MOUNT_COUNT = "mountcount";
	public static final String HDF_UNMOUNT_COUNT = "unmountcount";
	public static final String HDF_NEEDS_CLEANING = "needscleaning";
	public static final String HDF_VOLTAGE_VALUE = "voltage";
	public static final String HDF_TEMPERATURE_VALUE = "temperature";
	public static final String HDF_PHYSICAL_ADDRESS = "physicaladdress";
	public static final String HDF_PHYSICAL_ADDRESS_TYPE = "physicaladdresstype";
	public static final String HDF_LOGICAL_ADDRESS = "logicaladdress";
	public static final String HDF_LOGICAL_ADDRESS_TYPE = "logicaladdresstype";
	public static final String HDF_LINK_STATUS = "linkstatus";
	public static final String HDF_DUPLEX_MODE = "duplexmode";
	public static final String HDF_ZERO_BUFFER_CREDIT_COUNT = "zerobuffercreditcount";
	public static final String HDF_RECEIVED_PACKETS = "receivedpackets";
	public static final String HDF_TRANSMITTED_PACKETS = "transmittedpackets";
	public static final String HDF_LINK_SPEED = "linkspeed";
	public static final String HDF_BANDWIDTH = YAML_BANDWIDTH;

	// YAML metrics
	public static final String YAML_STATUS_INFORMATION = "StatusInformation";
	public static final String YAML_BATTERY_CHARGE = "hw.battery.charge";
	public static final String YAML_BATTERY_TIME_LEFT = "hw.battery.time_left";
	public static final String YAML_BATTERY_STATUS = "hw.status{hw.type=\"battery\"}";
	public static final String YAML_BLADE_STATUS = "hw.status{hw.type=\"blade\"}";
	public static final String YAML_BLADE_POWER_STATE = "hw.blade.power_state";
	public static final String YAML_CPU_POWER_LIMIT = "hw.power.limit{hw.type=\"cpu\"}";
	public static final String YAML_CPU_SPEED_LIMIT = "hw.cpu.speed.limit{limit_type=\"max\"}";
	public static final String YAML_CPU_ERRORS_LIMIT_DEGRADED =
		"hw.errors.limit{hw.type=\"cpu\", limit_type=\"degraded\"}";
	public static final String YAML_CPU_ERRORS_LIMIT_CRITICAL =
		"hw.errors.limit{hw.type=\"cpu\", limit_type=\"critical\"}";
	public static final String YAML_CPU_STATUS = "hw.status{hw.type=\"cpu\"}";
	public static final String YAML_CPU_PREDICTED_FAILURE = "hw.status{hw.type=\"cpu\", state=\"predicted_failure\"}";
	public static final String YAML_CPU_SPEED = "hw.cpu.speed";
	public static final String YAML_CPU_ERRORS = "hw.errors{hw.type=\"cpu\"}";
	public static final String YAML_CPU_POWER = "hw.power{hw.type=\"cpu\"}";
	public static final String YAML_CPU_ENERGY = "hw.energy{hw.type=\"cpu\"}";
	public static final String YAML_DISK_CONTROLLER_STATUS = "hw.status{hw.type=\"disk_controller\"}";
	public static final String YAML_LED_INDICATOR = "hw.led.indicator";
	public static final String YAML_LED_STATUS = "hw.status{hw.type=\"led\"}";
	public static final String YAML_LOGICALDISK_LIMIT = "hw.logical_disk.limit";
	public static final String YAML_LOGICALDISK_STATUS = "hw.status{hw.type=\"logical_disk\"}";
	public static final String YAML_LOGICALDISK_ERRORS = "hw.errors{hw.type=\"logical_disk\"}";
	public static final String YAML_LOGICALDISK_USAGE_FREE = "hw.logical_disk.usage{state=\"free\"}";
	public static final String YAML_LOGICALDISK_USAGE_USED = "hw.logical_disk.usage{state=\"used\"}";
	public static final String YAML_ENCLOSURE_STATUS = "hw.status{hw.type=\"enclosure\"}";
	public static final String YAML_ENCLOSURE_INTRUSION_STATUS = "hw.status{hw.type=\"enclosure\", state=\"open\"}";
	public static final String YAML_ENCLOSURE_ENERGY = "hw.enclosure.energy";
	public static final String YAML_ENCLOSURE_POWER = "hw.enclosure.power";
	public static final String YAML_MEMORY_ERRORS = "hw.errors{hw.type=\"memory\"}";
	public static final String YAML_MEMORY_PREDICTED_FAILURE =
		"hw.status{hw.type=\"memory\", state=\"predicted_failure\"}";
	public static final String YAML_MEMORY_STATUS = "hw.status{hw.type=\"memory\"}";
	public static final String YAML_OTHER_DEVICE_STATUS = "hw.status{hw.type=\"other_device\"}";
	public static final String YAML_OTHER_DEVICE_POWER = "hw.power{hw.type=\"other_device\"}";
	public static final String YAML_OTHER_DEVICE_ENERGY = "hw.energy{hw.type=\"other_device\"}";
	public static final String YAML_OTHER_DEVICE_USAGE_COUNT = "hw.other_device.uses";
	public static final String YAML_OTHER_DEVICE_VALUE = "hw.other_device.value";
	public static final String YAML_FAN_SPEED_LIMIT_DEGRADED = "hw.fan.speed.limit{limit_type=\"low.degraded\"}";
	public static final String YAML_FAN_SPEED_LIMIT_CRITICAL = "hw.fan.speed.limit{limit_type=\"low.critical\"}";
	public static final String YAML_FAN_SPEED_RATIO_LIMIT_DEGRADED =
		"hw.fan.speed_ratio.limit{limit_type=\"low.degraded\"}";
	public static final String YAML_FAN_SPEED_RATIO_LIMIT_CRITICAL =
		"hw.fan.speed_ratio.limit{limit_type=\"low.critical\"}";
	public static final String YAML_FAN_STATUS = "hw.status{hw.type=\"fan\"}";
	public static final String YAML_FAN_SPEED = "hw.fan.speed";
	public static final String YAML_FAN_SPEED_RATIO = "hw.fan.speed_ratio";
	public static final String YAML_GPU_STATUS = "hw.status{hw.type=\"gpu\"}";
	public static final String YAML_GPU_PREDICTED_FAILURE = "hw.status{hw.type=\"gpu\", state=\"predicted_failure\"}";
	public static final String YAML_GPU_ERRORS_CORRECTED = "hw.errors{hw.type=\"gpu\", hw.error.type=\"corrected\"}";
	public static final String YAML_GPU_ERRORS = "hw.errors{hw.type=\"gpu\"}";
	public static final String YAML_GPU_UTILIZATION_GENERAL = "hw.gpu.utilization{task=\"general\"}";
	public static final String YAML_GPU_UTILIZATION_DECODER = "hw.gpu.utilization{task=\"decoder\"}";
	public static final String YAML_GPU_UTILIZATION_ENCODER = "hw.gpu.utilization{task=\"encoder\"}";
	public static final String YAML_GPU_MEMORY_UTILIZATION = "hw.gpu.memory.utilization";
	public static final String YAML_GPU_IO_RECEIVE = "hw.gpu.io{direction=\"receive\"}";
	public static final String YAML_GPU_IO_TRANSMIT = "hw.gpu.io{direction=\"transmit\"}";
	public static final String YAML_GPU_POWER = "hw.power{hw.type=\"gpu\"}";
	public static final String YAML_GPU_ENERGY = "hw.energy{hw.type=\"gpu\"}";
	public static final String YAML_GPU_MEMORY_LIMIT = "hw.gpu.memory.limit";
	public static final String YAML_GPU_UTILIZATION_LIMIT_DEGRADED = "hw.gpu.utilization.limit{limit_type=\"degraded\"}";
	public static final String YAML_GPU_UTILIZATION_LIMIT_CRITICAL = "hw.gpu.utilization.limit{limit_type=\"critical\"}";
	public static final String YAML_GPU_MEMORY_UTILIZATION_LIMIT_DEGRADED =
		"hw.gpu.memory.utilization.limit{limit_type=\"degraded\"}";
	public static final String YAML_GPU_MEMORY_UTILIZATION_LIMIT_CRITICAL =
		"hw.gpu.memory.utilization.limit{limit_type=\"critical\"}";
	public static final String YAML_LUN_STATUS = "hw.status{hw.type=\"lun\"}";
	public static final String YAML_LUN_PATHS = "hw.lun.paths";
	public static final String YAML_LUN_PATHS_LIMIT_LOW_DEGRADED = "hw.lun.paths.limit{limit_type=\"low.degraded\"}";
	public static final String YAML_AVAILABLE_PATH_INFORMATION = "AvailablePathInformation";
	public static final String YAML_PHYSICAL_DISK_SIZE = "hw.physical_disk.size";
	public static final String YAML_PHYSICAL_DISK_STATUS = "hw.status{hw.type=\"physical_disk\"}";
	public static final String YAML_PHYSICAL_DISK_STATUS_PREDICTED_FAILURE =
		"hw.status{hw.type=\"physical_disk\", state=\"predicted_failure\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS = "hw.errors{hw.type=\"physical_disk\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_TRANSPORT =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"transport\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_ILLEGAL_REQUEST =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"illegal_request\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_NO_DEVICE =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"no_device\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_DEVICE_NOT_READY =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"device_not_ready\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_RECOVERABLE =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"recoverable\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_HARD =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"hard\"}";
	public static final String YAML_PHYSICAL_DISK_ERRORS_MEDIA =
		"hw.errors{hw.type=\"physical_disk\", hw.error.type=\"media\"}";
	public static final String YAML_PHYSICAL_DISK_ENDURANCE_UTILIZATION_REMAINING =
		"hw.physical_disk.endurance_utilization{state=\"remaining\"}";
	public static final String YAML_VM_POWER_STATE = "hw.vm.power_state";
	public static final String YAML_VM_POWER_RATIO = "hw.vm.power_ratio";
	public static final String YAML_VM_POWER = "hw.power{hw.type=\"vm\"}";
	public static final String YAML_VM_ENERGY = "hw.energy{hw.type=\"vm\"}";
	public static final String YAML_POWER_SUPPLY_STATUS = "hw.status{hw.type=\"power_supply\"}";
	public static final String YAML_POWER_SUPPLY_UTILIZATION = "hw.power_supply.utilization";
	public static final String YAML_POWER_SUPPLY_POWER = "hw.power_supply.power";
	public static final String YAML_POWER_SUPPLY_LIMIT = "hw.power_supply.limit";
	public static final String YAML_ROBOTICS_STATUS = "hw.status{hw.type=\"robotics\"}";
	public static final String YAML_ROBOTICS_MOVES = "hw.robotics.moves";
	public static final String YAML_ROBOTICS_ERRORS_LIMIT_DEGRADED =
		"hw.errors.limit{hw.type=\"robotics\", limit_type=\"degraded\"}";
	public static final String YAML_ROBOTICS_ERRORS_LIMIT_CRITICAL =
		"hw.errors.limit{hw.type=\"robotics\", limit_type=\"critical\"}";
	public static final String YAML_ROBOTICS_ERRORS = "hw.errors{hw.type=\"robotics\"}";
	public static final String YAML_TAPE_DRIVE_ERRORS_LIMIT_DEGRADED =
		"hw.errors.limit{hw.type=\"tape_drive\", limit_type=\"degraded\"}";
	public static final String YAML_TAPE_DRIVE_ERRORS_LIMIT_CRITICAL =
		"hw.errors.limit{hw.type=\"tape_drive\", limit_type=\"critical\"}";
	public static final String YAML_TAPE_DRIVE_STATUS = "hw.status{hw.type=\"tape_drive\"}";
	public static final String YAML_TAPE_DRIVE_ERRORS = "hw.errors{hw.type=\"tape_drive\"}";
	public static final String YAML_TAPE_DRIVE_OPERATIONS_MOUNT = "hw.tape_drive.operations{type=\"mount\"}";
	public static final String YAML_TAPE_DRIVE_OPERATIONS_UNMOUNT = "hw.tape_drive.operations{type=\"unmount\"}";
	public static final String YAML_TAPE_DRIVE_STATUS_NEEDS_CLEANING =
		"hw.status{hw.type=\"tape_drive\", state=\"needs_cleaning\"}";
	public static final String YAML_VOLTAGE_STATUS = "hw.status{hw.type=\"voltage\"}";
	public static final String YAML_VOLTAGE_VALUE = "hw.voltage";
	public static final String YAML_TEMPERATURE_VALUE = "hw.temperature";
	public static final String YAML_TEMPERATURE_STATUS = "hw.status{hw.type=\"temperature\"}";
	public static final String YAML_NETWORK_STATUS = "hw.status{hw.type=\"network\"}";
	public static final String YAML_NETWORK_UP = "hw.network.up";
	public static final String YAML_NETWORK_FULL_DUPLEX = "hw.network.full_duplex";
	public static final String YAML_NETWORK_ERROR_ZERO_BUFFER_CREDIT =
		"hw.errors{hw.type=\"network\", hw.error.type=\"zero_buffer_credit\"}";
	public static final String YAML_NETWORK_ERRORS = "hw.errors{hw.type=\"network\"}";
	public static final String YAML_NETWORK_RECEIVED_BYTES = "hw.network.io{direction=\"receive\"}";
	public static final String YAML_NETWORK_TRANSMITTED_BYTES = "hw.network.io{direction=\"transmit\"}";
	public static final String YAML_NETWORK_RECEIVED_PACKETS = "hw.network.packets{direction=\"receive\"}";
	public static final String YAML_NETWORK_TRANSMITTED_PACKETS = "hw.network.packets{direction=\"transmit\"}";
	public static final String YAML_NETWORK_BANDWIDTH_LIMIT = "hw.network.bandwidth.limit";

	// Mapping formats
	public static final String PERCENT_2_RATIO_FORMAT = "percent2Ratio(%s)";
	public static final String MEGA_HERTZ_2_HERTZ_FORMAT = "megaHertz2Hertz(%s)";
	public static final String MEBI_BYTE_2_BYTE_FORMAT = "mebiByte2Byte(%s)";
	public static final String BOOLEAN_FORMAT = "boolean(%s)";
	public static final String FAKE_COUNTER_FORMAT = "fakeCounter(%s)";
	public static final String LED_STATUS_FORMAT = "legacyLedStatus(%s)";
	public static final String LEGACY_INTRUSION_STATUS_FORMAT = "legacyIntrusionStatus(%s)";
	public static final String LEGACY_PREDICTED_FAILURE_FORMAT = "legacyPredictedFailure(%s)";
	public static final String LEGACY_POWER_SUPPLY_UTILIZATION_FORMAT = "legacyPowerSupplyUtilization(%s)";
	public static final String LEGACY_NEEDS_CLEANING_FORMAT = "legacyNeedsCleaning(%s)";
	public static final String RATE_FORMAT = "rate(%s)";
	public static final String COMPUTE_POWER_SHARE_RATIO_FORMAT = "computePowerShareRatio(%s)";
	public static final String LINK_STATUS_FORMAT = "legacyLinkStatus(%s)";
	public static final String FULL_DUPLEX_FORMAT = "legacyFullDuplex(%s)";
	public static final String MEGA_BIT_2_BIT_FORMAT = "megaBit2Bit(%s)";
	public static final String LEGACY_LED_STATUS_FORMAT = "legacyLedStatus(%s)";
}
