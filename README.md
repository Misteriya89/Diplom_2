# Diplom_2

Учебный проект по автотестированию API.

## Описание

Версия Java 11

#### Проект использует следующие библиотеки:

* JUnit 4
* RestAssured
* Allure
  Документация
  Ссылка на документацию проекта.

#### Запуск автотестов

Для запуска автотестов необходимо:

Скачать код

**git clone https://github.com/Misteriya89/Diplom_2.git**

Запустить команду в проекте

**mvn clean test**

Для создания отчета в Allure ввести команду

**mvn allure:report**

#### Структура проекта

```
src
|-- main
|   |-- java
|   |   |-- api
|   |   |   |-- model
|   |   |   |   |-- Ingredient.java
|   |   |   |   |-- Ingredients.java
|   |   |   |   |-- Order.java
|   |   |   |   |-- User.java
|   |   |   |-- steps
|   |   |   |   |-- OrderSteps.java
|   |   |   |   |-- UserSteps.java
|-- test
|   |-- java
|   |   |-- tests
|   |   |   |-- order
|   |   |   |   |-- CreateOrderTest.java
|   |   |   |   |-- GetUserOrderTest.java
|   |   |   |-- user
|   |   |   |   |-- ChangeUserDataTest.java
|   |   |   |   |-- CreateUserTest.java
|   |   |   |   |-- LoginUserTest.java
.gitignore
pom.xml
README.md
```

