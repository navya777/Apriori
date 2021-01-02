import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;


public class apri {
    private static List<List<String>> rawItemSet = new ArrayList<>();
    private List<List<String>> frequentItemSet1 = new ArrayList<>();
    private static Map<List<String>, Integer> frequentItemMap = new HashMap<>();
    private static Map<List<String>, Integer> allItemMap = new HashMap<>();
    private static Map<List<String>, Integer> frequentItemMap2 = new HashMap<>();
    private static Map<List<String>, Integer> allItemMap2 = new HashMap<>();
    private static double Support;
    private static double Confidence;

    private void loadingData(Connection connection) {
        List<Integer> rawTransactionId = new ArrayList<>();
        List<String> rawItemSetPerTransaction;
        try {
            Statement statement = connection.createStatement();
            String sql = "select distinct TID from transaction";
            ResultSet resultSetOfId = statement.executeQuery(sql);
            while (resultSetOfId.next()) {
                int transactionId = resultSetOfId.getInt("TID");
                rawTransactionId.add(transactionId);
            }

            for (int transactionId : rawTransactionId) {
                rawItemSetPerTransaction = new ArrayList<>();
                String sql2 = "select ItemSet from transaction where TID = " + transactionId;
                ResultSet resultSetOfItemName = statement.executeQuery(sql2);
                while (resultSetOfItemName.next()) {
                    String itemName = resultSetOfItemName.getString("ItemSet");
                    rawItemSetPerTransaction.add(itemName);
                }
                rawItemSet.add(rawItemSetPerTransaction);
                resultSetOfItemName.close();
            }
            System.out.println("TID  Items");
            for (int i = 0; i < rawItemSet.size(); i++) {
                System.out.println(rawTransactionId.get(i) + ": " + rawItemSet.get(i));
            }
            resultSetOfId.close();
            System.out.println("\nLoading data from database successfully!\n");
        } catch (SQLException e) {
            System.out.println("\nLoading data from database error\n");
            e.printStackTrace();
        }
    }
    private List<List<String>> getFrequentItemSet1() {
        List<String> temp = new ArrayList<>();
        List<String> tempAfterCompare;
        List<String> tempBeforeCompare;
        frequentItemMap = new HashMap<>();
        for (List<String> item : rawItemSet) {
            temp.addAll(item);
        }
        Set<String> removeDuplicateElements = new HashSet<>(temp);
        List<String> itemSetWithoutDuplicateElements = new ArrayList<>(removeDuplicateElements);
        for (String itemInList : itemSetWithoutDuplicateElements) {
            tempBeforeCompare = new ArrayList<>();
            tempBeforeCompare.add(itemInList);
            allItemMap.put(tempBeforeCompare,Collections.frequency(temp, itemInList));
            if ((Collections.frequency(temp, itemInList) >= Support)) {
                tempAfterCompare = new ArrayList<>();
                tempAfterCompare.add(itemInList);
                frequentItemMap.put(tempAfterCompare, Collections.frequency(temp, itemInList));
            }
        }
        frequentItemSet1.addAll(frequentItemMap.keySet());
        System.out.println("All ItemSet1");
        System.out.println(allItemMap);
        System.out.println();
        System.out.println("Frequent-ItemSet1");
        System.out.println(frequentItemMap);
        System.out.println("=========================================");
//        System.out.println("Frequent-ItemSet1" + frequentItemSet1);
        return frequentItemSet1;
    }
    private void getFrequentItemSetK(List<List<String>> frequentItemSet1) {
        if (frequentItemSet1 == null) {
            System.out.println("frequentItemSet1 is empty!");
            return;
        }
        List<List<String>> combinationResult = new ArrayList<>(getCombination(frequentItemSet1));
        List<List<String>> temp = new ArrayList<>();
        int sizeOfFrequent = 0;
        for (List<String> combinedItemSet : combinationResult) {
            int count = 0;
            for (List<String> rawTransaction : rawItemSet) {
                if (isInTheTransaction(rawTransaction, combinedItemSet)) {
                    count++;
                }
            }
            allItemMap2.put(combinedItemSet, count);
            if (count >= Support) {
                sizeOfFrequent++;
                temp.add(combinedItemSet);
                frequentItemMap2.put(combinedItemSet, count);
            }
        }
        if (sizeOfFrequent != 0) {
            combinationResult.clear();
            System.out.println("AllItemSet" + temp.get(0).size());
            for (List<String> key : allItemMap2.keySet()) {
                if (key.size() == temp.get(0).size()) {
                    int value = allItemMap2.get(key);
                    System.out.println(key + "=" + value);
                }
            }
            System.out.println();
            System.out.println("Frequent ItemSet" + temp.get(0).size());
            for (List<String> key : frequentItemMap2.keySet()) {
                if (key.size() == temp.get(0).size()) {
                    int value = frequentItemMap2.get(key);
                    System.out.println(key + "=" + value);
                }
            }
            System.out.println("=========================================");

//            System.out.println("Frequent-ItemSet" + temp.get(0).size());
//            for (List<String> item : temp) {
//                System.out.println(item+"");
//
//            }
            getFrequentItemSetK(temp);
        }
    }

    private boolean isInTheTransaction(List<String> transaction, List<String> Combination) {
        for (String item : Combination) {
            if (!transaction.contains(item)) {
                return false;
            }
        }
        return true;
    }

    private List<List<String>> getCombination(List<List<String>> list) {
        int k = 0;
        Set<String> firstPart;
        Set<Set<String>> resultTemp = new HashSet<>();
        List<List<String>> result = new ArrayList<>();
        while (k < list.size()) {
            for (List<String> aList : list) {
                for (int i = 0; i < aList.size(); i++) {
                    firstPart = new HashSet<>(list.get(k));
                    firstPart.add(aList.get(i));
                    if (firstPart.size() == (aList.size() + 1)) {
                        resultTemp.add(firstPart);
                    }
                }
            }
            k++;
        }
        for (Set<String> temp2 : resultTemp) {
            ArrayList<String> tempA = new ArrayList<>(temp2);
            result.add(tempA);
        }
        return result;
    }


    private List<List<String>> getSubset(List<String> key) {
        List<String> subset;
        List<List<String>> totalSubset = new ArrayList<>();
        if (key.size() == 0) {
            return totalSubset;
        }
        for (String item : key) {
            subset = new ArrayList<>();
            subset.add(item);
            totalSubset.add(subset);
        }
        List<List<String>> tempSubset = new ArrayList<>(totalSubset);
        for (int i = 0; i < key.size() - 2; i++) {
            List<List<String>> nextSubset;
            nextSubset = getCombination(tempSubset);
            totalSubset.addAll(nextSubset);
            tempSubset.clear();
            tempSubset.addAll(nextSubset);
        }
        return totalSubset;
    }

    private int getAssociationRule() {
        double support;
        double confidence;
        int count = 0;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Map<List<String>, Integer> workSpace = new HashMap<>(frequentItemMap2);
        List<String> key;
        for (Map.Entry<List<String>, Integer> entry : workSpace.entrySet()) {
            key = entry.getKey();
            List<List<String>> totalSubset = getSubset(key);
            for (List<String> leftItemSet : totalSubset) {
                for (List<String> rightItemSet : totalSubset) {
                    confidence = (double) entry.getValue() / (double) getValue(leftItemSet);
                    if (isEligible(leftItemSet, rightItemSet, key.size()) & confidence * 100 >= Confidence) {
                        count++;
                        support = ((double) entry.getValue() / (double) rawItemSet.size());
                        System.out.println(leftItemSet + "---->" + rightItemSet + "(" + decimalFormat.format(support * 100) + "%" + "," + decimalFormat.format(confidence * 100) + "%)");
                    }
                }
            }
        }
        return count;
    }

    private int getValue(List<String> key) {
        if (key.size() == 1) {
            for (List<String> key2 : frequentItemMap.keySet()) {
                if (key2.equals(key)) {
                    return frequentItemMap.get(key);
                }
            }
        }
        for (List<String> key2 : frequentItemMap2.keySet()) {
            HashSet<String> keyTemp = new HashSet<>(key2);
            if (keyTemp.containsAll(key) && key2.size() == key.size()) {
                return frequentItemMap2.get(key2);
            }
        }
        return 0;
    }

    private boolean isEligible(List<String> subset1, List<String> subset2, int ArrayListSize) {
        Set<String> temp = new HashSet<>();
        if ((subset1.size() + subset2.size()) > ArrayListSize) {
            return false;
        }
        temp.addAll(subset1);
        temp.addAll(subset2);
        return temp.size() == ArrayListSize;
    }
    public static void main(String[] args) {
        apri apriori = new apri();
        Scanner scanner = new Scanner(System.in);
        System.out.println("input 1 for loading data from Database, input 2 for loading data from Txt");
        int n = scanner.nextInt();
        if (n == 1) {
            System.out.println("Please select a database(1-5)");
            String database = scanner.next();
            try {
                Connection connection = Database.getConnection(database);
                if (connection != null)
                    apriori.loadingData(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        System.out.println("Please input minimum support in percentage");
        Support = scanner.nextDouble() * rawItemSet.size() / 100;
        System.out.println("Please input minimum confidence in percentage");
        Confidence = scanner.nextDouble();
        apriori.getFrequentItemSetK(apriori.getFrequentItemSet1());
        int count = apriori.getAssociationRule();
        if (count == 0) {
            System.out.println("There is no eligible association rule！");
        } else if (count == 1) {
            System.out.println("There is " + count + " association rule that meets the conditions.");
        } else {
            System.out.println("There are " + count + " association rules that meet the conditions.");
        }
    }
}

