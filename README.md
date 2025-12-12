# GPROG-Project

## Create new branch
Go to [issues](https://github.com/4Source/GPROG-Project/issues) section and select an existing issue you plan to work on, or create a new issue with a descriptive title and a short explanation of what should be changed (keep the changes small).
When you have opened or created your issue, click on `Create a branch` on the right side. Leave everything as it is and click `Create branch`.

<img width="1254" height="827" alt="create-brancht" src="https://github.com/user-attachments/assets/14010315-203c-4841-b13e-8ab7c63b4f32" />

Copy the commands shown and paste them into the command line in your local clone of the repository. This ensures you have the latest changes and checks out a new branch where you can work.

<img width="482" height="172" alt="checkout-branch" src="https://github.com/user-attachments/assets/939c8914-0075-44d9-a401-caa2d95ffac0" />

## Delete old branch
After you have merged a branch into `master`, make sure you do not continue making changes in that branch.

You can find out which branches are already merged (gone) by running the following commands:
```shell
git fetch --prune
git branch -vv
```
