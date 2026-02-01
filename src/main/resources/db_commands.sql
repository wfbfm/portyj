-- docker exec -it porty_db psql -U porty_user -d porty_db

CREATE TYPE account_type as ENUM ('ISA', 'NON_ISA');
CREATE TYPE price_source as ENUM ('YAHOO', 'LSE');

CREATE TABLE positions (
    id BIGSERIAL PRIMARY KEY,
    isin TEXT NOT NULL,
    asset_name TEXT NOT NULL,
    source price_source NOT NULL,
    symbol TEXT,
    quantity NUMERIC,
    purchase_price NUMERIC,
    currency TEXT,
    purchase_price_gbp NUMERIC,
    purchase_fx_rate NUMERIC,
    purchase_notional NUMERIC,
    purchase_notional_gbp NUMERIC,
    trade_date DATE,
    captured_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    account_type account_type NOT NULL
);

CREATE TABLE prices (
    id BIGSERIAL PRIMARY KEY,
    isin TEXT NOT NULL,
    current_price NUMERIC,
    previous_close NUMERIC,
    currency TEXT,
    current_price_gbp NUMERIC,
    previous_close_gbp NUMERIC,
    fx_rate NUMERIC,
    captured_at TIMESTAMPTZ NOT NULL DEFAULT now()
);