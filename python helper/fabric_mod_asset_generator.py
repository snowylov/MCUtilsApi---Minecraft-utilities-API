import json
import os

def write_block_model(name, parent="block/cube_all", texture="yourmodid:block/" + name):
    data = {
        "parent": parent,
        "textures": {
            "all": texture
        }
    }
    path = f"generated/models/block/{name}.json"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(data, f, indent=4)

def write_item_model(name, parent="item/generated", texture="yourmodid:item/" + name):
    data = {
        "parent": parent,
        "textures": {
            "layer0": texture
        }
    }
    path = f"generated/models/item/{name}.json"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(data, f, indent=4)

def write_blockstate(name, model="yourmodid:block/" + name):
    data = {
        "variants": {
            "": {
                "model": model
            }
        }
    }
    path = f"generated/blockstates/{name}.json"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(data, f, indent=4)

def write_lang_entry(entries, namespace="yourmodid"):
    path = f"generated/lang/en_us.json"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(entries, f, indent=4)

# Example usage:
if __name__ == "__main__":
    write_block_model("test_block")
    write_item_model("test_item")
    write_blockstate("test_block")
    write_lang_entry({"item.yourmodid.test_item": "Test Item", "block.yourmodid.test_block": "Test Block"})