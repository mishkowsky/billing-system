# Billing System 
## Nexign Bootcamp task

Все отдельные компоненты системы разбиты лишь на разные пакты в рамках одного модуля, однако являются независимым, общаются через брокера сообщений ActiveMQ Artemis.

В качестве базы данных абонентов использована in-memory база данных H2, [схема БД](https://dbdiagram.io/d/6442ebf96b31947051ff99af). Она конфигурируется с помощью `recources/data.sql` и `recources/import.sql`.

В роли базы данных пользователей CRM выступает `crm.UserRepository` с `Map` внутри.