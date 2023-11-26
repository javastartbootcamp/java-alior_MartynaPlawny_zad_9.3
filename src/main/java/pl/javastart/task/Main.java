package pl.javastart.task;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern[] patterns = new Pattern[]{
            Pattern.compile("(\\d{4})-(0[1-9]|1[0-2])-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})"),
            Pattern.compile("(\\d{2}).(0[1-9]|1[0-2]).(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})"),
            Pattern.compile("(\\d{4})-(0[1-9]|1[0-2])-(\\d{2}$)"),
            Pattern.compile("([t]([-|+][1-9]{1,2}[y|M|d|h|m|s]){1,6})")
    };

    public static void main(String[] args) {
        Main main = new Main();
        main.run(new Scanner(System.in));
    }

    public boolean validationWithRegex(String expression) {
        boolean patternFind = false;

        for (Pattern item : patterns) {
            Matcher matcher = item.matcher(expression);
            if (matcher.find()) {
                patternFind = true;
                break;
            }
        }
        return patternFind;
    }

    public static String reformatData(String data) {
        if (patterns[3].matcher(data).find()) {
            return calculateDifferenceInDatetime(data);
        } else if (patterns[2].matcher(data).find()) {
            return data + " 00:00:00";
        } else if (patterns[1].matcher(data).find()) {
            String[] parsedData = data.split("[. ]");
            return parsedData[2] + "-" + parsedData[1] + "-" + parsedData[0] + " " + parsedData[3];
        } else {
            return data;
        }
    }

    public static String calculateDifferenceInDatetime(String data) {
        String[] convertedData;
        convertedData = data.split("(?=[+\\-])");
        List<String> convertedDataList = new LinkedList<>(Arrays.asList(convertedData));
        convertedDataList.remove(0);
        LocalDateTime lt = LocalDateTime.now();
        for (int i = 0; i < convertedDataList.size(); i++) {
            List<String> singlePlusMinusAction;
            singlePlusMinusAction = Collections.singletonList(convertedDataList.get(i));


            if (Objects.equals(singlePlusMinusAction.get(0), "+")) {
                switch (singlePlusMinusAction.get(2)) {
                    case "y" -> lt = lt.plusYears(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "M" -> lt = lt.plusMonths(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "d" -> lt = lt.plusDays(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "h" -> lt = lt.plusHours(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "m" -> lt = lt.plusMinutes(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "s" -> lt = lt.plusSeconds(Long.parseLong(singlePlusMinusAction.get(1)));
                    default -> System.out.println("Nieznana jednostka");
                }
            } else {
                switch (singlePlusMinusAction.get(2)) {
                    case "y" -> lt = lt.minusYears(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "M" -> lt = lt.minusMonths(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "d" -> lt = lt.minusDays(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "h" -> lt = lt.minusHours(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "m" -> lt = lt.minusMinutes(Long.parseLong(singlePlusMinusAction.get(1)));
                    case "s" -> lt = lt.minusSeconds(Long.parseLong(singlePlusMinusAction.get(1)));
                    default -> System.out.println("Nieznana jednostka");
                }
                System.out.println("tutaj");
                System.out.println(singlePlusMinusAction.get(0));
            }
        }
        return String.valueOf(lt);
    }

    public void datetimeCalculations(String dateFromUserAfterReformat) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        // parsed String to dateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateFromUserAfterReformat, DateTimeFormatter.ofPattern(dateFormat));

        // local date time at your system's default time zone
        ZonedDateTime systemZoneDateTime = localDateTime.atZone(ZoneId.systemDefault());

        // value converted to other timezone while keeping the point in time
        ZonedDateTime utcDateTime = systemZoneDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime londonDateTime = systemZoneDateTime.withZoneSameInstant(ZoneId.of("Europe/London"));
        ZonedDateTime losAngelesDateTime = systemZoneDateTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime sidneyDateTime = systemZoneDateTime.withZoneSameInstant(ZoneId.of("Australia/Sydney"));

        //converting to final format
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String utc = format1.format(utcDateTime);
        String london = format1.format(londonDateTime);
        String losAngeles = format1.format(losAngelesDateTime);
        String sydney = format1.format(sidneyDateTime);

        System.out.println("Czas lokalny: " + dateFromUserAfterReformat);
        System.out.println("UTC: " + utc);
        System.out.println("Londyn: " + london);
        System.out.println("Los Angeles: " + losAngeles);
        System.out.println("Sydney: " + sydney);
    }

    public void run(Scanner scanner) {
        System.out.println("Wprowadź datę:");
        String dateFromUser = scanner.nextLine();

        while (!validationWithRegex(dateFromUser)) {
            System.out.println("Data w nieprawidłowym formacie, spróbuj ponownie");
            dateFromUser = scanner.nextLine();
        }

        String dateFromUserAfterReformat = reformatData(dateFromUser);

        try {
            datetimeCalculations(dateFromUserAfterReformat);
        } catch (DateTimeParseException ex) {
            System.out.println("Błąd parsowania daty lub czasu, kończę program.");
        }
    }
}