using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;

namespace Lab07
{
    internal class Program
    {
        private static void PrintDirectory(DirectoryInfo directory, string indent = "")
        {
            const string indentationLevel = "  ";

            var subDirs = directory.EnumerateDirectories()
                .ToArray();
            var subFiles = directory.EnumerateFiles()
                .ToArray();
            
            Console.WriteLine($"{indent}{directory.Name}/\t{subDirs.Length + subFiles.Length} elements");
            
            foreach (var dir in subDirs)
            {
                PrintDirectory(dir, indent + indentationLevel);
            }
            
            foreach (var file in subFiles)
            {
                Console.WriteLine($"{indent}{indentationLevel}{file.Name}\t{file.GetDosAttributes()}\t{file.Length}B");
            }
        }
        
        public static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.Error.WriteLine("Usage: Lab07 <directory>");
                return;
            }
            
            var directory = new DirectoryInfo(Path.GetFullPath(args[0]));
            PrintDirectory(directory);
            
            Console.WriteLine();
            
            var a = directory.EnumerateFiles().Select(x => (x.Name, x.Length));
            var b = directory.EnumerateDirectories().Select(x => (x.Name, x.EnumerateDirectories().Count() + x.EnumerateFiles().LongCount()));
            var collection = a.Concat(b)
                .OrderBy(x => x.Name.Length)
                .ThenBy(x => x.Name)
                .ToDictionary(x => x.Name, x => x.Item2);

            using (var ms = new MemoryStream())
            {
                var formatter = new BinaryFormatter();
                formatter.Serialize(ms, collection);

                ms.Position = 0;

                var tmp = formatter.Deserialize(ms) as Dictionary<string, long>;
                foreach (var pair in tmp)
                {
                    Console.WriteLine($"{pair.Key} -> {pair.Value}");
                }
            }
        }
    }
}