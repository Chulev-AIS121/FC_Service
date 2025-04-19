**Описание**
Сервис ms-client представляет собой REST API для работы с клиентами. Сервис поддерживает операции создания, обновления, поиска и получения клиентов, а также предоставляет Swagger-документацию и информацию о состоянии через Actuator.

**Требования**
+Java версии 17 или выше
+Spring Boot версии 2.6 или выше
+PostgreSQL версии 14 или выше

**Настройка проекта**
💡 Важно: можно запускать приложение с указанием переменных окружения:
export DB_URL=jdbc:postgresql://localhost:5432/ms_client?currentSchema=ms_client_schema
export DB_USERNAME=postgres
export DB_PASSWORD=Boxing2003
./mvnw spring-boot:run

Также можно изменить параметры подключения в src/main/resources/application.properties:
# Параметры подключения к PostgreSQL (можно переопределить через переменные окружения)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/ms_client?currentSchema=ms_client_schema}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:Boxing2003}

**Сценарии API**
POST /client: Создает нового клиента. Ожидает JSON с данными клиента (без id).
PUT /client/{id}: Обновляет клиента по id. Если клиент не активен, вернется ошибка 422. Если клиент не найден — 404.
GET /client/{id}: Получает клиента по id. Если клиент не найден — 404.
GET /client: Возвращает список клиентов с пагинацией (по умолчанию 50 на страницу).
GET /client/search?querySymbol=*: Ищет клиентов по fullName или shortName, которые содержат указанный символ.

**Логическая модель**
Client
-id: Идентификатор (UUID)
-fullName: Полное имя
-shortName: Сокращенное имя
-createDateTime: Дата создания
-updateDateTime: Дата обновления
-clientType: Тип клиента (IP или UL)
-inn: ИНН
-active: Действующий (по умолчанию true)

ClientType
-key: Идентификатор
-name: Наименование

**Миграции и Наполнение БД**
Миграции и данные заполняются с использованием Liquibase. Для генерации случайных данных используются CSV-файлы. Миграции можно найти в папке src/main/resources/db/changelog.

**Нагрузочное тестирование**
Нагрузочное тестирование выполнено с использованием JMeter. Сценарии тестирования можно найти в папке jmeter-scenarios.

**Запуск приложения**
Запуск на локальной машине: Убедитесь, что PostgreSQL настроен и доступен.
После настройки переменных окружения (DB_URL, DB_USERNAME, DB_PASSWORD), можно запускать:
./mvnw spring-boot:run

**Swagger UI**: После запуска приложения Swagger будет доступен по адресу:
http://localhost:8080/swagger-ui.html

**Тестирование**
Для тестирования API реализованы unit-тесты с использованием Spring Context и Zonky для эмуляции базы данных. Тесты покрывают как позитивные сценарии, так и обработку ошибок
