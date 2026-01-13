太好了 🎉
下面是一份**可以直接用于发布的英文 README**，内容覆盖功能说明、使用方式、支持范围和注意事项，适合上传到 JetBrains Marketplace / GitHub。

---

# CSS Modules Go To Definition (WebStorm / IntelliJ)

A productivity plugin for **WebStorm / IntelliJ IDEA** that enables **Ctrl / Cmd + Click navigation from CSS Modules class usage in React to the exact definition in CSS / Less / Scss / Stylus files**.

---

## ✨ Features

* 🔗 **Go to Definition for CSS Modules**

    * Navigate from `className={style.a_class}` or `style['a_class']`
    * Jump directly to the corresponding class definition in style files

* 🎨 **Multi-style support**

    * `.css`
    * `.less`
    * `.scss`
    * `.styl`

* 🧠 **Accurate class resolution**

    * Supports exact matching only:

        * `a_class` → `.a_class`
        * `a-class` → `.a-class`
        * `aClass` → `.aClass`
    * No aggressive or fuzzy name mapping

* 🌲 **Nested selector resolution**

    * Correctly resolves nested selectors using `&`
    * Example:

      ```less
      .a_demo-info {
        &-test {
          color: red;
        }
      }
      ```

      `style['a_demo-info-test']` → jumps to `&-test`

* 📦 **CSS Modules import based**

    * Works only when style files are imported via:

      ```ts
      import style from './index.less'
      ```
    * No need to scan the entire project

* 🛡 **Safe fallback**

    * If no matching class is found, the plugin does **nothing**
    * WebStorm’s default behavior remains untouched

---

## 🚀 Usage

In a React / JSX / TSX file:

```tsx
import style from './index.less'

<View className={style['item_info']} />
<View className={style.item_info} />
```

👉 **Ctrl + Click (Windows/Linux)**
👉 **Cmd + Click (macOS)**

You will be navigated to:

```less
.item_info {
  ...
}
```

or nested equivalents using `&`.

---

## 🧩 Supported Syntax

### Class access

* `style.className`
* `style['class_name']`

### Style imports

```ts
import style from './index.css'
import style from '@/components/foo/index.scss'
```

> `@` alias is resolved as the project `src` directory.

---

## 🧠 How It Works (Brief)

1. Detects CSS Modules class usage in JSX / TSX
2. Resolves the imported style file via ES `import`
3. Parses the CSS PSI tree
4. Recursively resolves nested selectors (`&`) upward
5. Navigates to the exact class definition

---

## ⚠️ Limitations

* Only supports **default CSS Modules imports**

  ```ts
  import style from './index.less'
  ```
* Does **not** support:

    * `import { style } from ...`
    * Global CSS classes
    * Runtime-generated class names

---

## 🛠 Development & Debugging

* Built with **IntelliJ Platform SDK**
* Tested on **WebStorm**
* Designed to fail safely and never override native IDE behavior

---

## 📦 Installation

### From Marketplace

> *(Once published)*
> Search for **CSS Modules Go To Definition**

### Manual

1. Download the plugin `.zip`
2. Settings → Plugins → Install Plugin from Disk
3. Restart IDE

---

## 📄 License

MIT License

---

## ❤️ Acknowledgements

Inspired by daily React + CSS Modules workflows and the need for **precise, predictable navigation**.

