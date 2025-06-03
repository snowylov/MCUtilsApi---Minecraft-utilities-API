import json
import os
import tkinter as tk
from tkinter import filedialog, messagebox

def ensure_path(path):
    os.makedirs(os.path.dirname(path), exist_ok=True)

def write_json_file(path, data):
    ensure_path(path)
    with open(path, "w") as f:
        json.dump(data, f, indent=4)

def write_block_model(name, parent, texture, output_folder):
    data = {
        "parent": parent,
        "textures": {
            "all": texture
        }
    }
    write_json_file(os.path.join(output_folder, "models/block", f"{name}.json"), data)

def write_item_model(name, parent, texture, output_folder):
    data = {
        "parent": parent,
        "textures": {
            "layer0": texture
        }
    }
    write_json_file(os.path.join(output_folder, "models/item", f"{name}.json"), data)

def write_blockstate(name, model, output_folder):
    data = {
        "variants": {
            "": {
                "model": model
            }
        }
    }
    write_json_file(os.path.join(output_folder, "blockstates", f"{name}.json"), data)

def write_lang_entry(entries, output_folder):
    write_json_file(os.path.join(output_folder, "lang", "en_us.json"), entries)

def run_gui():
    root = tk.Tk()
    root.title("Minecraft Asset Generator")
    root.geometry("400x300")

    def browse_json():
        path = filedialog.askopenfilename(filetypes=[("JSON Files", "*.json")])
        if path:
            json_path.set(path)

    def browse_output():
        path = filedialog.askdirectory()
        if path:
            output_path.set(path)

    def generate_from_json():
        try:
            with open(json_path.get(), "r") as f:
                config = json.load(f)

            output_folder = output_path.get()
            for block in config.get("blocks", []):
                write_block_model(block["name"], block.get("parent", "block/cube_all"),
                                  block.get("texture", f"{config['modid']}:block/{block['name']}"), output_folder)
                write_blockstate(block["name"], f"{config['modid']}:block/{block['model']}", output_folder)

            for item in config.get("items", []):
                write_item_model(item["name"], item.get("parent", "item/generated"),
                                 item.get("texture", f"{config['modid']}:item/{item['name']}"), output_folder)

            write_lang_entry(config.get("lang", {}), output_folder)
            messagebox.showinfo("Success", "Assets generated successfully!")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    json_path = tk.StringVar()
    output_path = tk.StringVar()

    tk.Label(root, text="JSON Config File").pack(pady=5)
    tk.Entry(root, textvariable=json_path, width=40).pack()
    tk.Button(root, text="Browse", command=browse_json).pack(pady=5)

    tk.Label(root, text="Output Folder").pack(pady=5)
    tk.Entry(root, textvariable=output_path, width=40).pack()
    tk.Button(root, text="Browse", command=browse_output).pack(pady=5)

    tk.Button(root, text="Generate", command=generate_from_json).pack(pady=20)

    root.mainloop()

if __name__ == "__main__":
    run_gui()