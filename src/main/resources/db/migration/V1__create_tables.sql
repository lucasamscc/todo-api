CREATE TABLE boards (
    id   UUID         NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_boards PRIMARY KEY (id)
);

CREATE TABLE columns (
    id       UUID         NOT NULL DEFAULT gen_random_uuid(),
    name     VARCHAR(255) NOT NULL,
    position INTEGER      NOT NULL,
    board_id UUID         NOT NULL,
    CONSTRAINT pk_columns PRIMARY KEY (id),
    CONSTRAINT fk_columns_board FOREIGN KEY (board_id)
        REFERENCES boards (id) ON DELETE CASCADE
);

CREATE TABLE tasks (
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    position   INTEGER      NOT NULL,
    created_at TIMESTAMPTZ,
    due_date   TIMESTAMPTZ,
    completed  BOOLEAN      NOT NULL DEFAULT FALSE,
    column_id  UUID         NOT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_column FOREIGN KEY (column_id)
        REFERENCES columns (id) ON DELETE CASCADE
);

CREATE TABLE task_tags (
    task_id UUID         NOT NULL,
    tag     VARCHAR(100) NOT NULL,
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id)
        REFERENCES tasks (id) ON DELETE CASCADE
);
