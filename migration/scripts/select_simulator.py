"""Choose an available iPhone; never pin a runner-specific simulator UUID."""
import json
import os
import re
import subprocess

inventory = json.loads(subprocess.check_output(["xcrun", "simctl", "list", "devices", "available", "--json"]))
candidates = []
for runtime, devices in inventory["devices"].items():
    if ".iOS-" not in runtime:
        continue
    version = tuple(map(int, re.findall(r"\d+", runtime.split("iOS-")[-1])))
    for device in devices:
        if device["name"].startswith("iPhone") and device.get("isAvailable", True):
            candidates.append((version, device["name"], device["udid"]))
if not candidates:
    raise SystemExit("No available iPhone simulator on this runner")
_, name, udid = max(candidates)
with open(os.environ["GITHUB_ENV"], "a") as environment:
    environment.write(f"IOS_SIMULATOR_ID={udid}\n")
print(f"Selected {name}: {udid}")
