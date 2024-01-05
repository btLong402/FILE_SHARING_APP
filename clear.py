import os
import shutil
import mysql.connector
import subprocess
def delete_folder_contents(folder_path):
    try:
        # Kiểm tra xem thư mục có tồn tại hay không
        if os.path.exists(folder_path):
            # Xóa toàn bộ nội dung trong thư mục
            for item in os.listdir(folder_path):
                item_path = os.path.join(folder_path, item)
                if os.path.isfile(item_path):
                    os.remove(item_path)
                elif os.path.isdir(item_path):
                    shutil.rmtree(item_path)
            print(f"Nội dung của thư mục {folder_path} đã được xóa thành công.")
        else:
            print(f"Thư mục {folder_path} không tồn tại.")
    except Exception as e:
        print(f"Lỗi: {e}")


def execute_mysql_script(host, user, password, database, sql_script_path):
    try:
        # Build the command to execute
        command = [
            'mysql',
            '-h', host,
            '-u', user,
            '-p' + password,
            database
        ]

        # Use subprocess to run the MySQL command with the script as input
        with open(sql_script_path, 'rb') as script:
            subprocess.run(command, stdin=script, check=True)

        print(f"MySQL script executed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Error executing MySQL script: {e}")



# Sử dụng công cụ
folder_to_clear = "./root"
mysql_host = "localhost"
mysql_user = "root"
mysql_password = "123456"
mysql_database = "ftpdb"
mysql_script_path = "./DB/create.sql"

delete_folder_contents(folder_to_clear)
execute_mysql_script(mysql_host, mysql_user, mysql_password, mysql_database, mysql_script_path)