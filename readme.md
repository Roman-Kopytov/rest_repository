REST task repository для T1 

RESTful сервис для управления задачами:

Task(id, title, description,userId)

1. POST /tasks — создание новой задачи.

2. GET /tasks/{id} — получение задачи по ID.

3. PUT /tasks/{id} — обновление задачи.

4. DELETE /tasks/{id} — удаление задачи.

5. GET /tasks — получение списка всех задач.