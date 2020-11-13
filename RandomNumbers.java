/*
Grupo 1
-------

Agenor de Oliveira Ikeda
Caio Henrique do Carmo Bastos Java8
Everton Ricardo Alauk Freire
Leonardo Vieira Mafra Alexandrino
Neilton Andrade Santos java11
Rogério Limas Moreira
Marilia Matos

*/

package edu.br.impacta.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class RandomNumbers {
    private static final ExecutorService executor = Executors.newWorkStealingPool(4);

    private static int getRandomNumber() {
        return (int) ((Math.random() * 100) + 1);
    }

    private static Future<List<Integer>> asyncRandomNumbersListGenerator() {
        List<Integer> finalList = new ArrayList<>();

        return executor.submit(() -> {
            while (finalList.size() < 5) {
                int randomNumber = getRandomNumber();
                if (!finalList.contains(randomNumber)) {
                    finalList.add(randomNumber);
                    System.out.println(String.format("Generated number: %d", randomNumber));
                    Thread.sleep(100);
                }
            }

            return finalList;
        });
    }

    public static void generateLists() {
        var resultLists = new HashSet<Future<List<Integer>>>();

        for (int i = 0; i < 5; i++) {
            resultLists.add(asyncRandomNumbersListGenerator());
        }

        while (resultLists.stream().anyMatch(match -> !match.isDone())) ;

        var finalList = resultLists
                .stream()
                .map(list -> {
                    try {
                        return list.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return new ArrayList<Integer>();
                })
                .collect(Collectors.flatMapping(
                        Collection::stream,
                        Collectors.toList()
                ));


        executor.shutdown();

        finalList.forEach(System.out::println);

    }

}

