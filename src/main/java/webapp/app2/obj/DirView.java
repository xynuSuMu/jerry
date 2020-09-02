package webapp.app2.obj;

import java.util.List;

/**
 * @Auther: chenlong
 * @Date: 2020/9/1 21:07
 * @Description:
 */
public class DirView {
    private String name;
    private String path;

    private boolean isDir;

    private List<DirView> list;

    public boolean isDir() {
        return isDir;
    }

    public List<DirView> getList() {
        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public void setList(List<DirView> list) {
        this.list = list;
    }

    public DirView(String name) {
        this.name = name;
    }

    public DirView(String name, boolean isDir) {
        this(name);
        this.isDir = isDir;
    }
}
