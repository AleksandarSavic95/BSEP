package ftn.bsep9.utility;


import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.QLog;

import java.util.ArrayList;
import java.util.List;


public class SearchParser {

    // using the query class we can create the filters
//    BooleanExpression filterByMACAddress = qLog.MACAddress.eq("\tC5:85:06:17:93:BF");
//    BooleanExpression filterByService = qLog.service.eq("\tstudent-service");
//    BooleanExpression filterByReferentService = qLog.service.eq("\treferent-service");
//    BooleanExpression filterByDate = qLog.date.between(LocalDateTime.now().minusDays(20), LocalDateTime.now());
//
//    // we can then pass the filters to the findAll() method
//    logs = (List<Log>) this.logsRepository.findAll(filterByMACAddress.and(filterByService).or(filterByReferentService)); // filterByDate
//
//            for (Log log :
//    logs) {
//        System.out.println(log);
//    }

        public static BooleanExpression parse(String searchString, QLog qLog) throws Exception {

            System.out.println(searchString);

            String[] wordsList = searchString.split(" ");

            List<String> logicalOperatorsList = new ArrayList<>();
            List<BooleanExpression> booleanExpressionsList = new ArrayList<>();

            int wordNumber = 0;
            BooleanExpression filter;

            do {
                System.out.println(wordsList[wordNumber]);
                System.out.println(wordsList[wordNumber+1]);
                System.out.println(wordsList[wordNumber+2]);
                switch (wordsList[wordNumber]) {
                    case "text":

                        wordNumber++;
                        System.out.println("\n T E X T");
                        // text = - [1525735397615] User with username: username1 has logged out.\n
                        if (wordsList[wordNumber].equals("=")) {
                            System.out.println("\t =");
                            wordNumber++;  // text = _rijec_;  _rijec_ ima indeks "text" + 2

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

//                            int openedSquareBracketIndex = searchBuilder.indexOf("%5B");
//                            int closedSquareBracketIndex = searchBuilder.indexOf("%5D");
//                            int colonIndex = searchBuilder.indexOf("%3A");
//                            searchBuilder.replace(openedSquareBracketIndex, 3, "[");
//                            searchBuilder.replace(closedSquareBracketIndex, 3, "]");
//                            searchBuilder.replace(colonIndex, 3, ":");
                            filter = qLog.text.eq(searchBuilder.toString());
                        }

                        // text contains oneWordText
                        else if(wordsList[wordNumber].equals("contains")) {
                            wordNumber++;
                            filter = qLog.text.contains(wordsList[wordNumber]);
                            wordNumber++;
                        }

                        // regex
                        else if (wordsList[wordNumber].equals("regex")) {

                            filter = qLog.text.matches(wordsList[wordNumber + 1]);
                        }
                        else
                            throw new Exception("Bad query formating for attribute: text");

                        booleanExpressionsList.add(filter);  // dodamo text filter u listu filtera
                        break;

                    case "date":  // date between 17.05.2018T20:30&18.5.2018T16:20:40
                        System.out.println("\n D A T E");
//                        if (wordsList[i].equals("before")) {
//
//                        }
//
//                        else if (wordsList[i].equals("after")) {
//
//                        }
//
//                        else if (wordsList[i].equals("between")) {
//
//                        }

                        break;
                    case "MACAddress":
                        System.out.println("\n M A C");
                        break;
                    case "service":
                        System.out.println("\n S E R V I C E");
                        break;
                    case "severityType":
                        System.out.println("\n S E V E R I T Y");
                        break;
                    default:
                        throw new Exception("Unmatched Log attribute >> " + wordsList[wordNumber]);
                }

                if (wordNumber < wordsList.length - 1) {
                    System.out.println(wordNumber);
                    System.out.println(wordsList.length);
                    System.out.println("adding :" + wordsList[wordNumber]);
                    logicalOperatorsList.add(wordsList[wordNumber]);
                }

                wordNumber++;  // povecamo broj da pogodimo sljedeci logicki operator (ili kraj rijeci)

            } while (wordNumber < wordsList.length);
//            text;
//            date;
//            MACAddress;
//            service;
//            severityType;
            // text = vrijednost and date between 17.05.2018T20:30&18.5.2018T16:20:40 or MACAddress =
//            for (int i = 0; i < wordsList.length - wordsList.length % 3; i = i + 4) {
//                System.out.println(wordsList[i]);
//                System.out.println(wordsList[i+1]);
//                System.out.println(wordsList[i+2]);
//
//
//                if (wordsList.length > 3) {
//                    System.out.println(wordsList[i+3]);
//                    if (i < 4)
//                        logicalOperatorsList.add(wordsList[3]);  // prvi logicki operator
//                    else
//                        logicalOperatorsList.add(wordsList[i - 1]);
//                }
//
//                BooleanExpression filter;
//                switch (wordsList[i]) {
//                    case "text":
//                        System.out.println("\n T E X T");
//
//                        if (wordsList[i + 1].contains("/.*"))
//                            filter = qLog.text.matches(wordsList[i + 2]);
//                        else if (wordsList[i + 1].equals("%3D"))
//                            filter = qLog.text.eq(wordsList[i + 2]);
//                        else if(wordsList[i + 1].equals("contains"))
//                            filter = qLog.text.contains(wordsList[i + 2]);
//                        else
//                            throw new Exception("Bad query formating for attribute: text");
//                        booleanExpressionsList.add(filter);
//                        break;
//
//                    case "date":  // date between 17.05.2018T20:30&18.5.2018T16:20:40
//                        System.out.println("\n D A T E");
////                        if (wordsList[i + 1].equals("before")) {
////
////                        }
////
////                        else if (wordsList[i + 1].equals("after")) {
////
////                        }
////
////                        else if (wordsList[i + 1].equals("between")) {
////
////                        }
//
//                        break;
//                    case "MACAddress":
//                        System.out.println("\n M A C");
//                        break;
//                    case "service":
//                        System.out.println("\n S E R V I C E");
//                        break;
//                    case "severityType":
//                        System.out.println("\n S E V E R I T Y");
//                        break;
//                    default:
//                        throw new Exception("Unmatched Log attribute");
//                }
//            }

//            text = vrijednost and date between 17.05.2018T20:30&18.5.2018T16:20:40 or MACAddress = C9:45:15:6A:93:17
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

                i++;
            }

            System.out.println("\n---------- ---------------- ----------");
            System.out.println(finalBooleanExpression.toString());
            System.out.println("---------- ---------------- ----------\n");

            return finalBooleanExpression;
        }


        public static void main(String[] args) {
            String asd = "asd fgh q w";
            if (asd.contains("d fg "))
                System.out.println("ASD!");
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