# Flussi di Login con Google

## 1. Primo Login (Registrazione)

```mermaid
sequenceDiagram
    participant U as Utente
    participant A as App Android
    participant G as Google
    participant B as Backend (API GATEWAY)
    participant MQ as Message Queue
    participant MS as Microservizio DB
    participant DB as Database

    U->>A: Clicca Login con Google
    A->>G: Richiesta ID Token
    G-->>A: Restituisce ID Token
    A->>B: Invio ID Token
    B->>G: Verifica ID Token  %% Flusso OAuth: verifica del token
    G-->>B: Restituisce informazioni utente
    B->>MQ: Invia messaggio per verifica utente
    MQ-->>MS: Messaggio di verifica utente
    MS->>DB: Verifica se esiste un utente
    alt Utente non esiste
        MS->>DB: Crea nuovo record utente
        DB-->>MS: Record creato
    end
    MS-->>MQ: 
    MQ-->>B: 
    B->>B: Generazione token di sessione JWT
    B-->>A: Invio token al Client
    A ->> A: Salva jwt in locale
    A-->>U: Utente può gestire i suoi vini

```

## 2. Secondo login (Accesso)
```mermaid

sequenceDiagram
    participant U as Utente
    participant A as App Android
    participant G as Google
    participant B as Backend (API GATEWAY)

    U->>A: Clicca Login con Google
    A->>G: Richiesta ID Token
    G-->>A: Restituisce ID Token
    A->>B: Invio ID Token
    B->>G: Verifica ID Token  %% Flusso OAuth: verifica del token
    G-->>B: Restituisce informazioni utente
    B->>MQ: Invia messaggio per accesso utente
    MQ-->>MS: Messaggio di accesso utente
    alt Utente esiste
        MS->>Database: Recupero dati utente
    end
    MS-->>MQ: 
    MQ-->>B: 
B->>B: Generazione nuovo token di sessione JWT
    B-->>A: Invio token al Client
    A ->> A: Salva jwt in locale
    A-->>U: Utente può gestire i suoi vini

``````