using System.IO;
using System.Linq;
using System.Text;

namespace Lab07
{
    public static class Extensions
    {
        public static FileInfo GetEldestElement(this DirectoryInfo directoryInfo)
        {
            return directoryInfo
                .EnumerateDirectories()
                .Select(x => x.GetEldestElement())
                .Concat(directoryInfo.EnumerateFiles())
                .OrderBy(x => x.CreationTimeUtc)
                .First();
        }

        public static string GetDosAttributes(this FileSystemInfo fileSystemInfo)
        {
            var attributes = fileSystemInfo.Attributes;
            var dosAttributes = new StringBuilder();
            dosAttributes.Append((attributes & FileAttributes.ReadOnly) != 0 ? 'R' : '-');
            dosAttributes.Append((attributes & FileAttributes.Archive) != 0 ? 'A' : '-');
            dosAttributes.Append((attributes & FileAttributes.Hidden) != 0 ? 'H' : '-');
            dosAttributes.Append((attributes & FileAttributes.System) != 0 ? 'S' : '-');
            return dosAttributes.ToString();
        }
    }
}