# SimpleCustomNPC 🎬

Mod Fabric untuk Minecraft **1.21.11** — cocok buat YouTuber yang mau bikin thumbnail atau screenshot kece!

## ✨ Fitur

| Fitur | Deskripsi |
|---|---|
| 📦 **Pasang kayak block** | Taruh NPC semudah naruh block biasa |
| 🎨 **Skin dari Username** | Masukkan username Minecraft → skin langsung di-fetch otomatis dari Mojang API |
| 🕹️ **Editor pose** | Klik kanan NPC → GUI slider untuk atur rotasi setiap bagian tubuh |
| 💪 **Kontrol lengkap** | Head (Yaw/Pitch), Body (Yaw), kedua tangan (Pitch/Yaw/Roll), kedua kaki |
| 🔤 **Display Name** | Bisa kasih nama floating di atas kepala NPC |
| 🧍 **Model Alex/Steve** | Toggle slim arms (model Alex) langsung dari GUI |

## 🚀 Cara Pakai

1. **Crafting**: Item `NPC Spawner` bisa di-give via `/give @s simplecustomnpc:npc_spawn_block`
2. **Place**: Taruh item ke dunia seperti block biasa → NPC langsung muncul
3. **Edit**: Klik kanan NPC atau block-nya → GUI editor terbuka
4. **Skin**: Buka tab **Skin**, ketik username Minecraft → klik Save
5. **Pose**: Buka tab **Pose**, geser slider sesuai keinginan → klik Save
6. **Remove**: Hancurkan block di bawah NPC → NPC ikut hilang

## 🛠️ Build

Butuh **Java 21**.

```bash
./gradlew build
```

Output ada di `build/libs/simplecustomnpc-1.0.0.jar`.

## 📋 Versi

| Komponen | Versi |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loom | 1.14.10 |
| Fabric Loader | 0.18.1 |
| Fabric API | 0.139.5+1.21.11 |
| Yarn Mappings | 1.21.11+build.4 |
| Gradle | 9.2 |
| Java | 21 |

## 📁 Struktur Proyek

```
src/
├── main/java/com/simplecustomnpc/
│   ├── SimpleCustomNpc.java          # Entry point (server)
│   ├── block/                         # NpcSpawnBlock + BlockEntity
│   ├── entity/                        # CustomNpcEntity
│   ├── item/                          # NpcSpawnItem
│   ├── network/                       # Packet definitions & server receiver
│   └── util/                          # NpcPoseData
└── client/java/com/simplecustomnpc/
    ├── SimpleCustomNpcClient.java     # Entry point (client)
    ├── client/gui/                    # NpcEditorScreen
    ├── entity/client/                 # CustomNpcRenderer
    └── network/                       # NpcClientNetworking
```
