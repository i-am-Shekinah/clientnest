-- Enable UUID generation (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE TYPE user_type AS ENUM ('SUPER_ADMIN', 'BUSINESS_OWNER', 'CLIENT');
CREATE TYPE booking_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED_BY_CLIENT',
    'CANCELLED_BY_BUSINESS',
    'NO_SHOW'
    );

-- =========================
-- USER TABLE
-- =========================
CREATE TABLE public.users (
    user_id                         UUID NOT NULL,
    first_name                      VARCHAR(100) NOT NULL,
    last_name                       VARCHAR(100) NOT NULL,
    email                           VARCHAR(255) NOT NULL,
    password                        VARCHAR(255) NOT NULL,
    user_type                       user_type NOT NULL,
    is_email_verified               BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted                      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ,
    deleted_at                      TIMESTAMPTZ,

    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

CREATE INDEX idx_users_type
    ON public.users(user_type)
    WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX uk_users_email
    ON public.users(email)
    WHERE is_deleted = FALSE;
CREATE INDEX idx_users_fullname_trgm
    ON public.users USING gin ((first_name || ' ' || last_name) gin_trgm_ops)
    WHERE is_deleted = FALSE;


-- =========================
-- BUSINESS TABLE
-- =========================
CREATE TABLE public.business (
    business_id                     UUID NOT NULL,
    business_name                   VARCHAR(255) NOT NULL,
    business_description            TEXT,
    business_email                  VARCHAR(255) NOT NULL,
    business_address                VARCHAR(100) NOT NULL,
    business_city                   VARCHAR(50) NOT NULL,
    business_state                  VARCHAR(50) NOT NULL,
    business_phone                  VARCHAR(50) NOT NULL,
    business_owner_id               UUID NOT NULL,
    business_url                    VARCHAR(255) NOT NULL,
    is_deleted                      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ,
    deleted_at                      TIMESTAMPTZ,

    CONSTRAINT pk_business PRIMARY KEY (business_id),
    CONSTRAINT fk_business_owner FOREIGN KEY (business_owner_id)
        REFERENCES public.users(user_id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_deleted_state CHECK (
        (is_deleted = FALSE AND deleted_at IS NULL)
            OR
        (is_deleted = TRUE AND deleted_at IS NOT NULL)
    )
);

CREATE INDEX idx_business_is_deleted ON public.business(is_deleted);
CREATE INDEX idx_business_name ON public.business(business_name);
CREATE INDEX idx_business_owner_id
    ON public.business(business_owner_id)
    WHERE is_deleted = FALSE;
CREATE INDEX idx_business_location
    ON public.business(business_state, business_city)
    WHERE is_deleted = FALSE;



-- =========================
-- SERVICE TABLE
-- =========================
CREATE TABLE public.service (
    service_id                      UUID NOT NULL,
    business_id                     UUID NOT NULL,
    service_name                    VARCHAR(255),
    duration_minutes                INT NOT NULL DEFAULT 1,
    price                           NUMERIC(10, 2),
    is_deleted                      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ,
    deleted_at                      TIMESTAMPTZ,

    CONSTRAINT pk_service PRIMARY KEY (service_id),
    CONSTRAINT fk_business FOREIGN KEY (business_id)
        REFERENCES public.business(business_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_duration_minutes CHECK ( duration_minutes > 0 ),
    CONSTRAINT chk_deleted_state CHECK (
        (is_deleted = FALSE AND deleted_at IS NULL)
            OR
        (is_deleted = TRUE AND deleted_at IS NOT NULL)
    )
);

CREATE INDEX idx_service_business_name
    ON public.service (business_id, service_name)
    WHERE is_deleted = FALSE;
CREATE INDEX idx_service_created_at
    ON public.service (business_id, created_at)
    WHERE is_deleted = FALSE;
CREATE INDEX idx_service_updated_at
    ON public.service (business_id, updated_at)
    WHERE is_deleted = FALSE;


-- =========================
-- BOOKING TABLE
-- =========================
CREATE TABLE public.booking (
    booking_id                      UUID NOT NULL,
    business_id                     UUID NOT NULL,
    service_id                      UUID NOT NULL,
    client_id                       UUID NOT NULL,
    booking_status                  booking_status NOT NULL DEFAULT 'PENDING',
    start_time                      TIMESTAMPTZ NOT NULL,
    end_time                        TIMESTAMPTZ NOT NULL,
    total_price                     NUMERIC(10, 2) NOT NULL,
    notes                           TEXT,
    is_deleted                      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ,
    deleted_at                      TIMESTAMPTZ,

    CONSTRAINT pk_booking PRIMARY KEY (booking_id),
    CONSTRAINT fk_booking_business FOREIGN KEY (business_id)
        REFERENCES public.business(business_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_booking_service FOREIGN KEY (service_id)
        REFERENCES public.service(service_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_booking_client FOREIGN KEY (client_id)
        REFERENCES public.users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_booking_times CHECK (end_time > start_time),
    CONSTRAINT chk_deleted_state CHECK (
        (is_deleted = FALSE AND deleted_at IS NULL)
            OR
        (is_deleted = TRUE AND deleted_at IS NOT NULL)
    )
);

CREATE INDEX idx_booking_business_schedule
    ON public.booking (business_id, start_time)
    WHERE is_deleted = FALSE;
CREATE INDEX idx_booking_client_history
    ON public.booking (client_id, start_time DESC)
    WHERE is_deleted = FALSE;
CREATE INDEX idx_booking_business_clients
    ON public.booking (business_id, client_id)
    WHERE is_deleted = FALSE;



-- =========================
-- CHAT_ROOM TABLE
-- =========================
CREATE TABLE public.chat_room(
    chat_room_id                        UUID NOT NULL,
    business_id                         UUID NOT NULL,
    client_id                           UUID NOT NULL ,
    created_at                          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_chat_room PRIMARY KEY (chat_room_id),
    CONSTRAINT fk_business FOREIGN KEY (business_id)
        REFERENCES public.business(business_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_client FOREIGN KEY (client_id)
        REFERENCES public.users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_business_client UNIQUE (business_id, client_id)
);



-- =========================
-- CHAT_MESSAGE TABLE
-- =========================
CREATE TABLE public.chat_message(
    message_id                          UUID NOT NULL,
    chat_room_id                        UUID NOT NULL,
    sender_id                           UUID NOT NULL,
    message_text                        TEXT NOT NULL,
    is_read                             BOOLEAN NOT NULL DEFAULT FALSE,
    is_edited                           BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted                          BOOLEAN NOT NULL DEFAULT FALSE,
    edited_at                           TIMESTAMPTZ,
    created_at                          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                          TIMESTAMPTZ,
    deleted_at                          TIMESTAMPTZ,

    CONSTRAINT pk_message PRIMARY KEY (message_id),
    CONSTRAINT fk_chat_room FOREIGN KEY (chat_room_id)
        REFERENCES public.chat_room(chat_room_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_sender_id FOREIGN KEY (sender_id)
        REFERENCES public.users(user_id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_deleted_state CHECK (
        (is_deleted = FALSE AND deleted_at IS NULL)
        OR
        (is_deleted = TRUE AND deleted_at IS NOT NULL)
    )
);

CREATE INDEX idx_chat_messages_stream ON public.chat_message (chat_room_id, created_at DESC);
CREATE INDEX idx_chat_messages_unread
    ON public.chat_message (chat_room_id)
    WHERE is_read = FALSE;




-- =========================
-- CHAT_MESSAGE TABLE
-- =========================
CREATE TABLE public.message_edit_history (
    history_id                          UUID NOT NULL,
    message_id                          UUID NOT NULL,
    previous_text                       TEXT NOT NULL,
    edited_at                           TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_history PRIMARY KEY (history_id),
    CONSTRAINT fk_message FOREIGN KEY (message_id)
        REFERENCES public.chat_message(message_id)
        ON DELETE CASCADE
);