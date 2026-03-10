package com.testcraft.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CellTest {

    @Test
    @DisplayName("Default constructor should create a cell with default values")
    void defaultConstructor() {
        Cell cell = new Cell();
        assertThat(cell.getRow()).isEqualTo(0);
        assertThat(cell.getCol()).isEqualTo(0);
        assertThat(cell.getWeight()).isEqualTo(0);
    }

    @Test
    @DisplayName("Parameterized constructor should create a cell with specified values")
    void parameterizedConstructor() {
        Cell cell = new Cell(1, 2);
        assertThat(cell.getRow()).isEqualTo(1);
        assertThat(cell.getCol()).isEqualTo(2);
        assertThat(cell.getWeight()).isEqualTo(0);
    }

    @Test
    @DisplayName("Parameterized constructor with weight should create a cell with specified values")
    void parameterizedConstructorWithWeight() {
        Cell cell = new Cell(1, 2, 3);
        assertThat(cell.getRow()).isEqualTo(1);
        assertThat(cell.getCol()).isEqualTo(2);
        assertThat(cell.getWeight()).isEqualTo(3);
    }

    @Test
    @DisplayName("Getters should return the correct values")
    void getters() {
        Cell cell = new Cell(1, 2);
        assertThat(cell.getRow()).isEqualTo(1);
        assertThat(cell.getCol()).isEqualTo(2);
    }

    @Test
    @DisplayName("Setters should update the correct values")
    void setters() {
        Cell cell = new Cell(1, 2);
        cell.setRow(3);
        cell.setCol(4);
        assertThat(cell.getRow()).isEqualTo(3);
        assertThat(cell.getCol()).isEqualTo(4);
    }

    @Test
    @DisplayName("Equals method should return true for equal objects")
    void equalsEqualObjects() {
        Cell cell1 = new Cell(1, 2);
        Cell cell2 = new Cell(1, 2);
        assertThat(cell1.equals(cell2)).isTrue();
    }

    @Test
    @DisplayName("Equals method should return false for unequal objects")
    void equalsUnequalObjects() {
        Cell cell1 = new Cell(1, 2);
        Cell cell2 = new Cell(3, 4);
        assertThat(cell1.equals(cell2)).isFalse();
    }

    @Test
    @DisplayName("Equals method should return false for null object")
    void equalsNullObject() {
        Cell cell = new Cell(1, 2);
        assertThat(cell.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Equals method should return false for different class object")
    void equalsDifferentClass() {
        Cell cell = new Cell(1, 2);
        Object obj = new Object();
        assertThat(cell.equals(obj)).isFalse();
    }

    @Test
    @DisplayName("HashCode method should return the correct hash code")
    void hashCodeMethod() {
        Cell cell = new Cell(1, 2);
        assertThat(cell.hashCode()).isEqualTo(Objects.hash(1, 2));
    }

    @Test
    @DisplayName("ToString method should return the correct string representation")
    void toStringMethod() {
        Cell cell = new Cell(1, 2);
        assertThat(cell.toString()).isEqualTo("(1,2)");
    }
}