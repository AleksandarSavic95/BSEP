package ftn.bsep9.utility;


import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.QLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class SearchParser {

        public static BooleanExpression parse(String searchString, QLog qLog) throws Exception {

            System.out.println(searchString);

            String[] wordsList = searchString.split(" ");

            List<String> logicalOperatorsList = new ArrayList<>();
            List<BooleanExpression> booleanExpressionsList = new ArrayList<>();

            Integer wordNumber = 0;
            BooleanExpression filter;

            Boolean firstNot = false;

            if (wordsList[0].equals("not")) {
                firstNot = true;
                wordNumber++;
            }

            do {
                switch (wordsList[wordNumber]) {
                    case "text":
                        wordNumber++;
                        System.out.println("\n T E X T");
                        // text = - [1525735397615] User with username: username1 has logged out.\n
                        switch (wordsList[wordNumber]) {
                            case "=":
                                System.out.println("\t =");
                                wordNumber++;

                                StringBuilder searchBuilder = new StringBuilder();
                                do {
                                    searchBuilder.append(wordsList[wordNumber]);
                                    searchBuilder.append(" ");
                                    wordNumber++;
                                } while(wordNumber < wordsList.length &&
                                        (!
                                            (wordsList[wordNumber].equals("and")
                                            || wordsList[wordNumber].equals("or")
                                            || wordsList[wordNumber].equals("not"))
                                        ) );

                                filter = qLog.text.eq(searchBuilder.toString());
                                break;
                            case "contains":  // text contains oneWordText
                                wordNumber++;
                                filter = qLog.text.contains(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "regex":  // regex
                                wordNumber++;
                                filter = qLog.text.matches(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            default:
                                throw new Exception("Bad query formating for attribute: text");
                        }
                        break;

                    case "date":  // date between 17.05.2018. 20:30 and 18.05.2018. 16:20:40
                        System.out.println("\n D A T E");
                        wordNumber++;
                        switch (wordsList[wordNumber]) {
                            case "before":
                                wordNumber++;
                                filter = qLog.date.before(LocalDateTime.parse(
                                        wordsList[wordNumber] + " " + wordsList[wordNumber + 1],
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")));
                                wordNumber++;
                                wordNumber++;
                                break;
                            case "after":
                                wordNumber++;
                                filter = qLog.date.after(LocalDateTime.parse(
                                        wordsList[wordNumber] + " " + wordsList[wordNumber + 1],
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")));
                                wordNumber++;
                                wordNumber++;
                                break;
                            case "between":
                                wordNumber++;
                                if ( ! (wordsList[wordNumber + 2].equals("and")) )
                                    throw new DateTimeParseException("Date separator is not 'and'", wordsList[wordNumber + 2], 1);
                                filter = qLog.date.between(
                                        LocalDateTime.parse(wordsList[wordNumber] + " " + wordsList[wordNumber + 1],
                                                DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")),
                                        LocalDateTime.parse(wordsList[wordNumber + 3] + " " + wordsList[wordNumber + 4],
                                                DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")));
                                wordNumber = wordNumber + 5;
                                break;
                            default:
                                throw new Exception("Bad query formatting for attribute: date");
                        }
                        break;

                    case "MACAddress":
                        System.out.println("\n M A C");
                        wordNumber++;
                        switch (wordsList[wordNumber]) {
                            case "=":
                                wordNumber++;
                                filter = qLog.MACAddress.eq(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "contains":
                                wordNumber++;
                                filter = qLog.MACAddress.contains(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "regex":  // regex
                                wordNumber++;
                                filter = qLog.MACAddress.matches(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            default:
                                throw new Exception("Bad query formatting for attribute: MACAddress");
                        }
                        break;

                    case "service":
                        System.out.println("\n S E R V I C E");
                        wordNumber++;
                        switch (wordsList[wordNumber]) {
                            case "=":
                                wordNumber++;
                                filter = qLog.service.eq(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "contains":
                                wordNumber++;
                                filter = qLog.service.contains(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "regex":  // regex
                                wordNumber++;
                                filter = qLog.service.matches(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            default:
                                throw new Exception("Bad query formatting for attribute: service");
                        }
                        break;

                    case "severityType":
                        System.out.println("\n S E V E R I T Y");
                        wordNumber++;
                        switch (wordsList[wordNumber]) {
                            case "=":
                                wordNumber++;
                                filter = qLog.severityType.eq(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "contains":
                                wordNumber++;
                                filter = qLog.severityType.contains(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            case "regex":  // regex
                                wordNumber++;
                                filter = qLog.severityType.matches(wordsList[wordNumber]);
                                wordNumber++;
                                break;
                            default:
                                throw new Exception("Bad query formatting for attribute: severityType");
                        }
                        break;

                    default:
                        throw new Exception("Unmatched Log attribute: " + wordsList[wordNumber]);
                }

                if (firstNot) {
                    booleanExpressionsList.add(filter.not());  // dodamo negiran filter u listu filtera
                    firstNot = false;
                }
                else
                    booleanExpressionsList.add(filter);  // dodamo filter u listu filtera

                if (wordNumber < wordsList.length - 1) {
                    System.out.println("adding: " + wordsList[wordNumber]);
                    logicalOperatorsList.add(wordsList[wordNumber]);
                }

                wordNumber++;  // povecamo broj da pogodimo sljedeci logicki operator (ili kraj rijeci)

            } while (wordNumber < wordsList.length);

//          text = vrijednost and date between 17.05.2018T20:30 and 18.5.2018T16:20:40 or MACAddress = C9:45:15:6A:93:17
            int i = 0;
            BooleanExpression finalBooleanExpression = booleanExpressionsList.get(i);
            for (String logicalOperator : logicalOperatorsList) {
                i++;
                switch (logicalOperator) {
                    case "and":
                        finalBooleanExpression = finalBooleanExpression.and(booleanExpressionsList.get(i));
                        break;
                    case "or":
                        finalBooleanExpression = finalBooleanExpression.or(booleanExpressionsList.get(i));
                        break;
                    case "not":
                        finalBooleanExpression = finalBooleanExpression.and(booleanExpressionsList.get(i).not());
                        break;
                    default:
                        throw new Exception("Bad logical operator >> " + logicalOperator);
                }
            }

            System.out.println("\n---------- ---------------- ----------");
            System.out.println(finalBooleanExpression.toString());

            return finalBooleanExpression;
        }

}

/*
            * nek zasvira sto sviraca
            * sve dok zora svice
            * rodila se cee rkaa
            * tatiinome zii mce
            *
            * svi
            * buduci moo mci
            * nek adoobro znajuu
            *
            * de vojkace
            * biii tiii
            * najlee psau
            * kraa
            * aaaa
            * juuuu*/