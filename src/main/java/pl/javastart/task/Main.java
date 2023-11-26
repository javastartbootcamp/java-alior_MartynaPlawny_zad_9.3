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
            Pattern.compile("([t]([-|+][1-9]{1,2}[y|M|d|h|m|s]){0,6})")
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

        for (String s : convertedDataList) {
            List<Character> singlePlusMinusAction = new ArrayList<>(s.chars().mapToObj(c -> (char) c).toList());
            String operator = String.valueOf(singlePlusMinusAction.get(0));
            String symbolOfTime = String.valueOf(singlePlusMinusAction.get(singlePlusMinusAction.size() - 1));
            singlePlusMinusAction.remove(0);
            singlePlusMinusAction.remove(singlePlusMinusAction.size() - 1);

            StringBuilder valueToChange = new StringBuilder();
            for (Character item : singlePlusMinusAction) {
                valueToChange.append(item);
            }

            if (Objects.equals(operator, "+")) {
                switch (symbolOfTime) {
                    case "y" -> lt = lt.plusYears(Long.parseLong(String.valueOf(valueToChange)));
                    case "M" -> lt = lt.plusMonths(Long.parseLong(String.valueOf(valueToChange)));
                    case "d" -> lt = lt.plusDays(Long.parseLong(String.valueOf(valueToChange)));
                    case "h" -> lt = lt.plusHours(Long.parseLong(String.valueOf(valueToChange)));
                    case "m" -> lt = lt.plusMinutes(Long.parseLong(String.valueOf(valueToChange)));
                    case "s" -> lt = lt.plusSeconds(Long.parseLong(String.valueOf(valueToChange)));
                    default -> System.out.println("Nieznana jednostka: " + symbolOfTime);
                }
            } else {
                switch (symbolOfTime) {
                    case "y" -> lt = lt.minusYears(Long.parseLong(String.valueOf(valueToChange)));
                    case "M" -> lt = lt.minusMonths(Long.parseLong(String.valueOf(valueToChange)));
                    case "d" -> lt = lt.minusDays(Long.parseLong(String.valueOf(valueToChange)));
                    case "h" -> lt = lt.minusHours(Long.parseLong(String.valueOf(valueToChange)));
                    case "m" -> lt = lt.minusMinutes(Long.parseLong(String.valueOf(valueToChange)));
                    case "s" -> lt = lt.minusSeconds(Long.parseLong(String.valueOf(valueToChange)));
                    default -> System.out.println("Nieznana jednostka: " + symbolOfTime);
                }
            }
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lt.format(format);
    }

    public void datetimeCalculations(String dateFromUserAfterReformat) {

        // parsed String to dateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateFromUserAfterReformat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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