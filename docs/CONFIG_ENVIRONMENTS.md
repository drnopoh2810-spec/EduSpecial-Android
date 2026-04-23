# Config Environments (Staging / Production)

This project now supports separate runtime config templates per environment.

## Files

- `configs/config.staging.json`
- `configs/config.production.json`
- `config.json` (active config consumed by backend/app flow)

## How to switch active config

Run from repository root:

- Staging:
  - `.\scripts\switch_config.ps1 -Environment staging`
- Production:
  - `.\scripts\switch_config.ps1 -Environment production`

This copies the selected file to `config.json`.

## Required value replacement before use

Replace all `REPLACE_*` placeholder values in both environment files:

- Firebase identifiers (`project_number`, `application_id`, `api_key`)
- OAuth client IDs (`web_client_id`, `android_client_id`)
- Shared secret (`shared_secret_hex`)
- Cloudinary/Algolia runtime values
- Backend URLs and JWT metadata (`issuer`, `audience`, `jwks_url`)

## Security rules

- Never commit private keys, service-account JSON, or admin API keys.
- Keep secrets in backend environment variables (see `server_secret_refs`).
- Rotate `shared_secret_hex` when promoting config to production.
