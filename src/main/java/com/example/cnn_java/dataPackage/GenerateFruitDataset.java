package com.example.cnn_java.dataPackage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GenerateFruitDataset {
  public static void main(String[] args) {
    Random random = new Random(42);
    int numSamples = 500;
    List<Fruit> redApples = generateFruitData("Red", "Apple", 3, 6, numSamples, random);
    List<Fruit> yellowApples = generateFruitData("Yellow", "Apple", 3, 7, numSamples, random);
    List<Fruit> greenApples = generateFruitData("Green", "Apple", 1, 3, numSamples, random);
    List<Fruit> oranges = generateFruitData("Orange", "Orange", 3, 7, numSamples, random);
    List<Fruit> yellowBananas = generateFruitData("Yellow", "Banana", 8, 10, numSamples, random);
    List<Fruit> greenBananas = generateFruitData("Green", "Banana", 4, 8, numSamples, random);
    List<Fruit> allFruitData = new ArrayList<>();
    allFruitData.addAll(redApples);
    allFruitData.addAll(yellowApples);
    allFruitData.addAll(greenApples);
    allFruitData.addAll(oranges);
    allFruitData.addAll(yellowBananas);
    allFruitData.addAll(greenBananas);
    Collections.shuffle(allFruitData);
    saveToFruitCSV("tricky_fruit_dataset.csv", allFruitData);
    displayFruitData(allFruitData);
  }

  private static List<Fruit> generateFruitData(String color, String fruitType, int sweetnessMin, int sweetnessMax, int numSamples, Random random) {
    List<Fruit> fruitData = new ArrayList<>();
    for (int i = 0; i < numSamples; i++) {
      float sweetness = sweetnessMin + (sweetnessMax - sweetnessMin) * random.nextFloat();
      fruitData.add(new Fruit(sweetness, color, fruitType));
    }
    return fruitData;
  }

  private static void saveToFruitCSV(String fileName, List<Fruit> fruitData) {
    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write("Sweetness,Color,Fruit\n");
      for (Fruit fruit : fruitData) {
        writer.write(fruit.toString() + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void displayFruitData(List<Fruit> fruitData) {
    System.out.println("Sweetness,Color,Fruit");
    for (int i = 0; i < Math.min(5, fruitData.size()); i++) {
      System.out.println(fruitData.get(i).toString());
    }
  }
}

final class Fruit {
  private final float sweetness;
  private final String color;
  private final String fruitType;

  public Fruit(float sweetness, String color, String fruitType) {
    this.sweetness = sweetness;
    this.color = color;
    this.fruitType = fruitType;
  }

  @Override
  public String toString() {
    return sweetness + "," + color + "," + fruitType;
  }
}
